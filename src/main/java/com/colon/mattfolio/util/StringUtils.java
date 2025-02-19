package com.colon.mattfolio.util;

import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringEscapeUtils;

public class StringUtils {

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

}
