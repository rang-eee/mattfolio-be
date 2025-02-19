package com.colon.mattfolio.util.interfaces;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;

/**
 * CreateXlsxUtil 인터페이스
 */
public interface CreateXlsxUtilInterface {

    /**
     * row에 데이터를 저장 한 후의 콜백
     * 
     * @param row : 해당 row 객체
     * @param rowIndex : 해당 row의 인덱스
     * @return
     */
    public default SXSSFRow rowCallBack(SXSSFRow row, int rowIndex) {
        return row;
    }

    /**
     * cell에 데이터를 저장 한 후의 콜백
     * 
     * @param cell : 해당 cell 객체
     * @param cellIndex : 해당 cell의 인덱스
     * @return
     */
    public default SXSSFCell cellCallBack(SXSSFCell cell, int cellIndex) {
        return cell;
    }
}
