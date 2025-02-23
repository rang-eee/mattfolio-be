package com.colon.mattfolio.util;

import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.text.StringEscapeUtils;

import jakarta.annotation.Nullable;

public class StringUtils {

    private static final char FOLDER_SEPARATOR_CHAR = '/';

    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * 문자열이 null이거나 빈 문자열인지 확인합니다.
     * 
     * @param str 확인할 문자열
     * @return 문자열이 null이거나 빈 문자열이면 true, 그렇지 않으면 false
     */
    public static boolean isNullEmpty(String str) {
        return str == null || str.trim()
            .isEmpty();
    }

    /**
     * 문자열이 null이거나 빈 문자열이 아닌지 확인합니다.
     * 
     * @param str 확인할 문자열
     * @return 문자열이 null이거나 빈 문자열이 아니면 true, 그렇지 않으면 false
     */
    public static boolean isNotNullEmpty(String str) {
        return !isNullEmpty(str);
    }

    /**
     * 문자열을 지정한 문자열로 시작하는지 확인합니다.
     * 
     * @param str 확인할 문자열
     * @param prefix 시작 문자열
     * @return 지정한 문자열로 시작하면 true, 그렇지 않으면 false
     */
    public static boolean startsWith(String str, String prefix) {
        if (isNotNullEmpty(str))
            return false;

        return str != null && str.startsWith(prefix);
    }

    /**
     * 문자열을 지정한 문자열로 끝나는지 확인합니다.
     * 
     * @param str 확인할 문자열
     * @param suffix 끝 문자열
     * @return 지정한 문자열로 끝나면 true, 그렇지 않으면 false
     */
    public static boolean endsWith(String str, String suffix) {
        if (isNotNullEmpty(str))
            return false;

        return str != null && str.endsWith(suffix);
    }

    /**
     * 문자열 리스트를 지정한 구분자로 결합합니다.
     * 
     * @param list 문자열 리스트
     * @param delimiter 구분자
     * @return 결합된 문자열
     */
    public static String join(List<String> list, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (String str : list) {
            if (isNotNullEmpty(str)) {
                joiner.add(str);
            }
        }
        return joiner.toString();
    }

    /**
     * 모든 공백을 제거하는 메소드
     * 
     * @param input 입력 문자열
     * @return 공백이 제거된 문자열
     */
    public static String removeAllSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }

    /**
     * 모든 공백을 제거하는 메소드 (Integer 입력)
     * 
     * @param input 입력 정수
     * @return 공백이 제거된 문자열
     */
    public static String removeAllSpaces(Integer input) {
        if (input == null) {
            return null;
        }
        // Integer를 String으로 변환한 후 공백 제거
        return removeAllSpaces(input.toString());
    }

    /**
     * 입력된 문자열의 HTML 엔티티를 디코딩하는 메소드.
     * 
     * @description 일반적으로 2중첩까지 escape가 발생함.
     * @param input HTML 엔티티가 포함된 입력 문자열
     * @return 디코딩된 문자열 (입력 값이 null인 경우 null 반환)
     */
    public static String unescapeHtml(String input) {
        if (input == null) {
            return null;
        }

        String decoded = input;

        for (int i = 0; i < 5; i++) {
            String temp = StringEscapeUtils.unescapeHtml3(decoded);
            temp = StringEscapeUtils.unescapeHtml4(decoded);

            temp = temp.replace("&apos;", "'");
            temp = temp.replace("&amp;", "&");
            temp = temp.replace("&gtdot;", "≳"); // HTML5 추가 기호
            temp = temp.replace("&lesdot;", "⩿");
            temp = temp.replace("&rarr;", "→");
            temp = temp.replace("&harr;", "↔");
            temp = temp.replace("&NewLine;", "\n");
            temp = temp.replace("&nabla;", "∇"); // 수학 기호
            temp = temp.replace("&notin;", "∉");
            temp = temp.replace("&infin;", "∞");
            temp = temp.replace("&prod;", "∏");

            if (temp.equals(decoded)) {
                break; // 더 이상 디코딩할 필요가 없으면 중단
            }
            decoded = temp;
        }

        return decoded;
    }

    /**
     * 주어진 Java 리소스 경로에서 파일명을 추출합니다. <br/>
     * 예: {@code "mypath/myfile.txt" &rarr; "myfile.txt"}.
     * 
     * @param path 파일 경로 (값이 {@code null}일 수 있음)
     * @return 추출된 파일명 또는 파일명이 없을 경우 {@code null}
     */
    @Nullable
    public static String getFilename(@Nullable String path) {
        if (path == null) {
            return null;
        }

        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

    /**
     * 주어진 Java 리소스 경로에서 파일 확장자를 추출합니다. <br/>
     * 예: "mypath/myfile.txt" &rarr; "txt".
     * 
     * @param path 파일 경로 (값이 {@code null}일 수 있음)
     * @return 추출된 파일 확장자 또는 확장자가 없을 경우 {@code null}
     */
    @Nullable
    public static String getFilenameExtension(@Nullable String path) {
        if (path == null) {
            return null;
        }

        int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (extIndex == -1) {
            return null;
        }

        int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR);
        if (folderIndex > extIndex) {
            return null;
        }

        return path.substring(extIndex + 1);
    }

    /**
     * 카멜 케이스를 스네이크 케이스로 변환하는 메소드 <br/>
     * 예: myVariableName → my_variable_name
     * 
     * @param input 카멜 케이스 문자열
     * @return 스네이크 케이스 문자열
     */
    public static String camelToSnake(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("([a-zA-Z])([0-9])", "$1_$2") // 문자 + 숫자 사이에 "_" 추가
            .replaceAll("([0-9])([a-zA-Z])", "$1_$2") // 숫자 + 문자 사이에 "_" 추가
            .replaceAll("([a-z])([A-Z])", "$1_$2") // 소문자 + 대문자 사이에 "_" 추가
            .toLowerCase();
    }

    /**
     * 스네이크 케이스를 카멜 케이스로 변환하는 메소드 <br/>
     * 예: my_variable_name → myVariableName
     * 
     * @param input 스네이크 케이스 문자열
     * @return 카멜 케이스 문자열
     */
    public static String snakeToCamel(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] parts = input.split("_");
        StringBuilder camelCaseString = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1)
                .toUpperCase())
                .append(parts[i].substring(1));
        }
        return camelCaseString.toString();
    }

}
