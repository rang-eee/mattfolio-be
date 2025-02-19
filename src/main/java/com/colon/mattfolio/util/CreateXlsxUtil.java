package com.colon.mattfolio.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.colon.mattfolio.annotation.XlsxMappingField;
import com.colon.mattfolio.util.interfaces.CreateXlsxUtilInterface;

import lombok.Getter;

/**
 * 엑셀 생성 유틸
 */
public abstract class CreateXlsxUtil<T> implements CreateXlsxUtilInterface {
    private Collection<T> data;
    // private Deque<T> data;
    private Class<?> dataClass;
    private Class<XlsxMappingField> mapper = XlsxMappingField.class;

    private static final int IN_MEMORY_ROW_LIMIT = 100;

    private static final int NEXT_SHEET_LIMIT = 1000000;

    private Map<String, Integer> columnIndexMap = null;

    private boolean isContinue;

    private final ByteArrayOutputStream out;

    @Getter
    private SXSSFWorkbook workbook;

    /**
     * 생성자 (마지막 시트 또는 한 시트만 저장할 경우 사용)
     * 
     * @param data : 엑셀에 쓸 데이터
     */
    public CreateXlsxUtil(Collection<T> data, SXSSFWorkbook workbook) {
        this.data = data;
        Type type = this.getClass()
            .getGenericSuperclass();
        this.dataClass = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        this.workbook = (workbook == null ? new SXSSFWorkbook(IN_MEMORY_ROW_LIMIT) : workbook);
        this.isContinue = false;
        this.out = new ByteArrayOutputStream();
    }

    /**
     * 생성자 (한 파일 내에 여러 시트를 쌓을 경우 사용)
     * 
     * @param data 엑셀에 쓸 데이터
     * @param isContinue 더 작성할 데이터가 있는지 여부
     */
    public CreateXlsxUtil(Collection<T> data, SXSSFWorkbook workbook, boolean isContinue) {
        this.data = data;
        Type type = this.getClass()
            .getGenericSuperclass();
        this.dataClass = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        this.workbook = (workbook == null ? new SXSSFWorkbook(IN_MEMORY_ROW_LIMIT) : workbook);
        this.isContinue = isContinue;
        this.out = new ByteArrayOutputStream();
    }

    /**
     * ResponseEntity<Resource> 엑셀 데이터를 다운로드 시키는 ResponseEntity를 반환하는 함수
     * 
     * @param fileName : 파일 이름
     * @param cacheMaxAge : 캐시 사용시 캐시 시간
     * @return ResponseEntity<Resource>
     * @throws IOException
     */
    public ResponseEntity<Resource> createXlsx(String fileName, Duration cacheMaxAge) throws IOException {
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
        CacheControl cache;
        if (cacheMaxAge == null) {
            cache = CacheControl.noCache();
        } else {
            cache = CacheControl.maxAge(cacheMaxAge)
                .cachePublic()
                .mustRevalidate();
        }

        // 파일 이름 URL 인코딩 후 '+'를 '%20'으로 대체
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
            .replace("+", "%20") + ".xlsx";

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .cacheControl(cache)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName)
            .body(resource);
    }

    /**
     * ResponseEntity<Resource> 엑셀 데이터를 다운로드 시키는 ResponseEntity를 반환하는 함수
     * 
     * @param fileName : 파일 이름
     * @return ResponseEntity<Resource>
     * @throws IOException
     */
    public ResponseEntity<Resource> createXlsx(String fileName) throws IOException {
        return this.createXlsx(fileName, null);
    }

    /**
     * 시트에 데이터를 쓰는 함수
     * 
     * @return CreateXlsx
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public CreateXlsxUtil<T> insertSheetData() throws IOException, IllegalAccessException {
        return this.insertSheetData("sheet");
    }

    /**
     * 시트에 데이터를 쓰는 함수
     * 
     * 처음 설계는 insertSheetData함수를 여러번 호출 할 수 있는 거였으나
     * 
     * 그러한 설계 방식으로 구현하기 위해서는 Collection의 제너릭 타입을 생성자에서 결정하는 것이 아닌 insertSheetData 함수를 호출하기 전에 알아야 한다.
     * 
     * 결국 다양한 제너릭 타입의 Collection으로 insertSheetData 함수를 여러번 호출하려면 다양한 제너릭 타입의 Collection을 허용하도록 수정이 필요
     * 
     * @param sheetNAme : 시트 이름
     * @param out : 엑셀 데이터를 받을 out stream
     * @return CreateXlsx
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public CreateXlsxUtil<T> insertSheetData(String sheetName) throws IOException, IllegalAccessException {
        try {
            int sheetIndex = 1;
            // createHead 함수에서 0번째 row를 생성하기 때문에 1부터 시작을 한다.
            int rowIndex = 1;
            // SXSSFSheet sheet = workbook.createSheet(sheetName + "_" + sheetIndex);
            SXSSFSheet sheet = workbook.createSheet(sheetName);
            columnIndexMap = this.createHead(sheet);
            // will probably slow things down
            // workbook.setCompressTempFiles(true) // temp files will be gzipped

            // for (int rowNum = 0, rowLen = data.size(); rowNum < rowLen; rowNum += 1) {
            for (Iterator<T> iter = data.iterator(); iter.hasNext();) {
                SXSSFRow row = sheet.createRow(rowIndex);
                // T item = data.get(rowNum);
                T item = iter.next();
                Field[] fields = dataClass.getDeclaredFields();
                for (int cellNum = 0, cellLen = fields.length; cellNum < cellLen; cellNum += 1) {
                    // 필드 가져오기
                    Field field = fields[cellNum];

                    if (!field.isAnnotationPresent(mapper)) {
                        // 맵핑을 위한 어노테이션이 없을시 컨티뉴
                        continue;
                    }
                    // 프라이빗 필드 엑세스 true 설정
                    field.setAccessible(true);
                    // 해당 필드에 들어있는 값 꺼내기 (setAccessible = true 설정 필요)
                    Object value = field.get(item);
                    // 프라이빗 필드 엑세스 false 설정
                    field.setAccessible(false);
                    int cellIndex = columnIndexMap.get(field.getName());
                    SXSSFCell cell = row.createCell(cellIndex);
                    if (value != null) {
                        this.setCellAnyValue(cell, value);
                    }
                    this.cellCallBack(cell, cellIndex);
                }
                this.rowCallBack(row, rowIndex);
                rowIndex += 1;
                if (rowIndex % NEXT_SHEET_LIMIT == 0) {
                    sheetIndex += 1;
                    sheet = workbook.createSheet(sheetName + "_" + sheetIndex);
                    rowIndex = 1;
                    this.createHead(sheet);
                    // this.insertSheetData(sheetName, 1, out, (sheetIndex + 1));
                    // break;
                }
            }

            workbook.write(out);
            out.flush();

            return this;

        } finally {
            if (workbook != null && !isContinue) {
                workbook.close();
                workbook.dispose(); // 임시 파일 삭제
                workbook = null;
            }
        }
    }

    /**
     * cell에 데이터를 저장시키는 함수
     * 
     * @param cell : cell 객체
     * @param value : cell에 쓸 값
     */
    private void setCellAnyValue(SXSSFCell cell, Object value) {
        if (value.getClass()
            .equals(Boolean.class)) {
            cell.setCellValue((Boolean) value);

        } else if (value.getClass()
            .equals(Long.class)) {
            cell.setCellValue(Double.valueOf((Long) value));

        } else if (value.getClass()
            .equals(Integer.class)) {
            cell.setCellValue(Double.valueOf((Integer) value));

        } else if (value.getClass()
            .equals(Byte.class)) {
            cell.setCellValue(Double.valueOf((Byte) value));

        } else if (value.getClass()
            .equals(Short.class)) {
            cell.setCellValue(Double.valueOf((Short) value));

        } else if (value.getClass()
            .equals(Double.class)) {
            cell.setCellValue((Double) value);

        } else if (value.getClass()
            .equals(Float.class)) {
            cell.setCellValue(Double.valueOf((Float) value));

        } else if (value.getClass()
            .equals(Character.class)) {
            cell.setCellValue(String.valueOf((Character) value));

        } else if (value.getClass()
            .equals(String.class)) {
            cell.setCellValue((String) value);

        } else if (value.getClass()
            .equals(Date.class)) {
            cell.setCellValue((Date) value);

        } else if (value.getClass()
            .equals(LocalDateTime.class)) {
            cell.setCellValue((LocalDateTime) value);

        } else if (value.getClass()
            .equals(LocalDate.class)) {
            cell.setCellValue((LocalDate) value);

        } else if (value.getClass()
            .equals(RichTextString.class)) {
            cell.setCellValue((RichTextString) value);

        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 엑셀 상단 해드(첫번째 로우)를 작성하는 함수
     * 
     * @param sheet
     * @return
     */
    private Map<String, Integer> createHead(Sheet sheet) {

        Row row = sheet.createRow(0);
        Field[] fields = dataClass.getDeclaredFields();
        Map<String, Integer> columnIndexMap = new HashMap<>();

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int cellNum = 0, cellLen = fields.length; cellNum < cellLen; cellNum += 1) {
            Field field = fields[cellNum];

            if (!field.isAnnotationPresent(mapper)) {
                // 맵핑을 위한 어노테이션이 없을시 컨티뉴
                continue;
            }
            // xlsx 컬럼과 매핑할 어노테이션 선언
            XlsxMappingField xlsxMapper = field.getAnnotation(mapper);
            int index = xlsxMapper.index();
            String columnName = xlsxMapper.column();
            Cell cell = row.createCell(xlsxMapper.index());

            // 어노테이션에 column 값이 비어있는 경우 변수 이름을 column 값으로 설정
            if (columnName.equals("")) {
                columnName = field.getName();
            }

            cell.setCellValue(columnName);
            cell.setCellStyle(headStyle);

            columnIndexMap.put(field.getName(), index);
        }
        return columnIndexMap;
    }
}
