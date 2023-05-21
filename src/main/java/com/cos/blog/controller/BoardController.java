package com.cos.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.model.Board;
import com.cos.blog.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	// 컨트롤러에서 세션을 어떻게 찾는지?
	@GetMapping({ "", "/" })
	public String index(Model model,
			@PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) { // @AuthenticationPrincipal
																											// PrincipalDetail
																											// principal
		System.out.println(pageable);
		Page<Board> boardList = boardService.글목록(pageable);

		int nowPage = boardList.getPageable().getPageNumber() + 1; // 현재페이지 : 0 에서 시작하기에 1을 더해준다.
		int firstlistpage = 1;
		int lastlistpage = 3;
		boolean listpagecheckflg = false;
		//System.out.printf("firstlistpage : %d lastlistpage : %d %n",firstlistpage,lastlistpage);
		//System.out.printf("getTotalPages : %d nowPage : %d %n",boardList.getTotalPages() ,nowPage);
		// 페이지 번호 리스트틀 10개씩 출력하도록 한다.
		// 마지막 리스트가 10개 미만일 경우는 남은 번호만 출력하도록 한다.
		while (listpagecheckflg == false) {
			if (boardList.getTotalPages() == 0) {
				lastlistpage = 1;
				listpagecheckflg = true;
			}
			if (lastlistpage > boardList.getTotalPages()) {
				lastlistpage = boardList.getTotalPages();
			}
			//System.out.printf("firstlistpage : %d lastlistpage : %d %n",firstlistpage,lastlistpage);
			if (nowPage >= firstlistpage && nowPage <= lastlistpage) {
				listpagecheckflg = true;
			} else {
				firstlistpage += 3;
				lastlistpage += 3;
			}
		}
		//System.out.printf("firstlistpage : %d lastlistpage : %d %n",firstlistpage,lastlistpage);
		// 현재 페이지 번호
		model.addAttribute("nowlistpageno", nowPage);
		// 총 페이지
		model.addAttribute("totalpagesize", boardList.getTotalPages());
		// 페이지 번호 리스트 (첫)
		model.addAttribute("firstlistpage", firstlistpage);
		// 페이지 번호 리스트 (마지막)
		model.addAttribute("lastlistpage", lastlistpage);
		// 페이지, 게시글 정보
		model.addAttribute("boards", boardList);

		return "index"; // viewResolver 작동!!
	}

	@GetMapping("/board/{id}")
	public String findById(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		return "board/detail";
	}
	
	// USER 권한이 필요
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String updateForm(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		return "board/updateForm";
	}
}
