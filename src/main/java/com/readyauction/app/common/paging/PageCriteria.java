package com.readyauction.app.common.paging;

import lombok.Data;

@Data
public class PageCriteria {
    private final int page;				// 현재 페이지 번호
    private final int limit;			// 한 페이지에 보여줄 게시물 수
    private final int totalCount;		// 전체 게시물 수
    private final String url;           // 링크에 작성될 url (검색 쿼리스트링이 포함될 수 있다.)

    private int maxPage;				// 가장 마지막 페이지
    private int pagebarSize;			// 한 번에 보여줄 페이징 버튼의 갯수
    private int pagebarStart;			// 페이징 버튼의 시작하는 페이지 수
    private int pagebarEnd;				// 페이징 버튼의 마지막 페이지 수

    public PageCriteria(int page, int limit, int totalCount, String url) {
        this.page = page;
        this.limit = limit;
        this.totalCount = totalCount;
        this.url = url + (url.contains("?") ? "&page=" : "?page="); // 검색어 쿼리스트링이 있는 경우는 &page=를 추가한다.
        this.maxPage = (int) Math.ceil((double) totalCount / limit);
        this.pagebarSize = 5;
        this.pagebarStart = (this.page - 1) / pagebarSize * pagebarSize + 1;
        this.pagebarEnd = pagebarStart + pagebarSize - 1;
    }
}
