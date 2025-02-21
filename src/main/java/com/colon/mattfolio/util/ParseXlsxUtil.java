package com.colon.mattfolio.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.common.annotation.XlsxReadMappingField;
import com.colon.mattfolio.common.exception.ExcelException;

/**
 * 엑셀 파싱 유틸리티
 * 
 * 엑셀 파일을 DTO 리스트로 변환합니다.
 * 
 * 주요 기능: - MultipartFile로부터 엑셀 데이터를 읽어 DTO 리스트로 반환 - 엑셀의 헤더와 DTO 필드 매핑 - 엑셀 데이터 유효성 검사 및 오류 처리
 */
public class ParseXlsxUtil {

    /**
     * MultipartFile을 받아 엑셀 데이터를 List<T>로 변환합니다.
     *
     * @param <T> DTO 타입
     * @param file 엑셀 파일 (MultipartFile)
     * @param clazz DTO 클래스 타입
     * @return List<T> 파싱된 DTO 리스트
     */
    public static <T> List<T> parseToList(MultipartFile file, Class<T> clazz) {
        // 파일이 비어있는지 확인
        if (file == null || file.isEmpty()) {
            throw new ExcelException(ExcelException.Reason.EMPTY_FILE); // 파일이 비어있음
        }

        // 확장자 확인
        if (!file.getOriginalFilename()
            .endsWith(".xlsx")
                && !file.getOriginalFilename()
                    .endsWith(".xls")) {
            throw new ExcelException(ExcelException.Reason.NOT_EXCEL_EXTENSION); // 확장자가 유효하지 않음
        }

        try (InputStream inputStream = file.getInputStream()) {
            return parseXlsxToList(inputStream, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException(ExcelException.Reason.INVALID_RESOURCE.getMessage(), e); // 파일 읽기 실패
        }
    }

    /**
     * InputStream을 받아 엑셀 데이터를 List<T>로 변환합니다.
     *
     * @param <T> DTO 타입
     * @param inputStream 엑셀 파일의 InputStream
     * @param clazz DTO 클래스 타입
     * @return List<T> 파싱된 DTO 리스트
     */
    private static <T> List<T> parseXlsxToList(InputStream inputStream, Class<T> clazz) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<T> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트만 처리
            Iterator<Row> rowIterator = sheet.iterator();

            // 헤더 처리
            Row headerRow = rowIterator.hasNext() ? rowIterator.next() : null;
            if (headerRow == null || headerRow.getPhysicalNumberOfCells() == 0) {
                throw new ExcelException(ExcelException.Reason.INVALID_HEADER); // 헤더가 유효하지 않음
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                String headerValue = cell.getStringCellValue()
                    .trim();
                if (headerValue.isEmpty()) {
                    throw new ExcelException(ExcelException.Reason.INVALID_HEADER); // 헤더가 비어있음
                }
                headers.add(headerValue);
            }

            // 데이터 행 처리
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T instance = mapRowToDto(row, headers, clazz);
                dataList.add(instance);
            }
        } catch (ExcelException e) {
            throw e; // 이미 발생한 익셉션은 그대로 전달
        } catch (Exception e) {
            throw new IllegalArgumentException(ExcelException.Reason.INVALID_RESOURCE.getMessage(), e); // 파일 읽기 실패
        }

        return dataList;
    }

    /**
     * 각 행(Row)을 DTO 객체로 매핑합니다.
     *
     * @param <T> DTO 타입
     * @param row 엑셀 행 데이터
     * @param headers 엑셀 헤더 목록
     * @param clazz DTO 클래스 타입
     * @return T - DTO 객체
     */
    private static <T> T mapRowToDto(Row row, List<String> headers, Class<T> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            T instance = clazz.getDeclaredConstructor()
                .newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(XlsxReadMappingField.class)) {
                    XlsxReadMappingField mapping = field.getAnnotation(XlsxReadMappingField.class);
                    int colIndex = headers.indexOf(mapping.column());

                    if (colIndex < 0) {
                        throw new ExcelException(ExcelException.Reason.HEADER_MISMATCH); // 헤더-필드 매핑 실패
                    }

                    Cell cell = row.getCell(colIndex);
                    field.setAccessible(true);

                    try {
                        // 셀 값을 필드 타입에 맞게 변환
                        Object cellValue = convertCellValue(getCellValue(cell, field.getType()), field.getType());
                        field.set(instance, cellValue);

                    } catch (Exception e) {
                        throw new IllegalArgumentException("필드 매핑 중 오류 발생: 필드 이름=" + field.getName() + ", 셀 값=" + cell, e);
                    }

                }
            }

            return instance;
        } catch (ExcelException e) {
            throw e; // 이미 발생한 익셉션은 그대로 전달
        } catch (Exception e) {
            throw new IllegalArgumentException(ExcelException.Reason.ROW_MAPPING_FAIL.getMessage(), e); // 행 매핑 실패
        }
    }

    /**
     * 셀 데이터를 필드 타입에 맞게 변환합니다.
     *
     * @param cell 셀 데이터
     * @param fieldType 필드 타입
     * @return Object - 필드 타입에 맞는 값
     */
    private static Object getCellValue(Cell cell, Class<?> fieldType) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue()
                .trim();

        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) { // 날짜 형식 처리
                if (fieldType == LocalDate.class) {
                    return cell.getLocalDateTimeCellValue()
                        .toLocalDate(); // LocalDate로 변환
                } else if (fieldType == LocalDateTime.class) {
                    return cell.getLocalDateTimeCellValue(); // LocalDateTime으로 변환
                } else {
                    return cell.getDateCellValue(); // 기본 Date 반환
                }
            } else {
                double numericValue = cell.getNumericCellValue();
                if (fieldType == LocalDate.class || fieldType == LocalDateTime.class) {
                    // 날짜로 직접 변환 시도
                    return parseExcelNumericDate(numericValue, fieldType);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    return (int) numericValue;
                } else if (fieldType == Long.class || fieldType == long.class) {
                    return (long) numericValue;
                } else if (fieldType == Float.class || fieldType == float.class) {
                    return (float) numericValue;
                } else if (fieldType == Double.class || fieldType == double.class) {
                    return numericValue;
                } else {
                    return numericValue; // 기본 double 반환
                }
            }

        case BOOLEAN:
            return cell.getBooleanCellValue(); // Boolean 값 처리

        case BLANK:
        default:
            return null; // 빈 셀 처리
        }
    }

    private static Object parseExcelNumericDate(double numericValue, Class<?> fieldType) {
        try {
            // 엑셀의 날짜 기준은 1900년 1월 1일 (숫자 값 1.0)
            LocalDateTime baseDate = LocalDateTime.of(1900, 1, 1, 0, 0);
            long daysSinceBase = (long) numericValue - 2; // 엑셀은 1900년 2월 29일을 잘못 포함 (조정 필요)
            long secondsInDay = (long) ((numericValue - daysSinceBase - 2) * 86400);

            LocalDateTime result = baseDate.plusDays(daysSinceBase)
                .plusSeconds(secondsInDay);
            if (fieldType == LocalDate.class) {
                return result.toLocalDate(); // LocalDate로 변환
            } else if (fieldType == LocalDateTime.class) {
                return result; // LocalDateTime으로 변환
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 LocalDateTime 형식입니다: " + numericValue, e);

        }
        return null;
    }

    private static Object convertCellValue(Object cellValue, Class<?> fieldType) {
        if (cellValue == null) {
            return null;
        }

        if (fieldType == String.class) {
            return cellValue.toString(); // 어떤 타입이든 String으로 변환
        } else if (fieldType == Integer.class || fieldType == int.class) {
            if (cellValue instanceof Number) {
                return ((Number) cellValue).intValue();
            }
            return Integer.parseInt(cellValue.toString());
        } else if (fieldType == Double.class || fieldType == double.class) {
            if (cellValue instanceof Number) {
                return ((Number) cellValue).doubleValue();
            }
            return Double.parseDouble(cellValue.toString());
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            if (cellValue instanceof Boolean) {
                return cellValue;
            }
            return Boolean.parseBoolean(cellValue.toString());
        }

        // 다른 타입은 지원하지 않으면 예외 발생
        throw new IllegalArgumentException("매핑할 수 없는 필드 타입: " + fieldType.getName());
    }

}
