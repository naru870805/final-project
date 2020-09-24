package project.sungho.problem_solve_module.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import project.sungho.mainController.MainControllerImpl;
import project.sungho.pro_collection_module.service.ProCollection_Service;
import project.sungho.pro_collection_module.vo.ProCollection_VO;
import project.sungho.problem_solve_module.service.Problem_Service;
import project.sungho.problem_solve_module.vo.ProblemExample_VO;
import project.sungho.problem_solve_module.vo.Problem_VO;

@Controller
public class PS_ControllerImpl implements PS_Controller {
	private static final Logger logger = LoggerFactory.getLogger(PS_ControllerImpl.class);
	
	@Autowired
	Problem_Service problem_Service;
	@Autowired
	ProCollection_Service proCollection_Service;
	
	@Autowired
	Problem_VO problem_VO;
	@Autowired
	ProblemExample_VO problemExample_VO;
	@Autowired
	ProCollection_VO proCollection_VO;


	@Override
	@RequestMapping(value = "**/c001_003.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String category = request.getParameter("category");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("category", category);
		List<Problem_VO> list = problem_Service.searchListCategory(searchMap);
		
		ModelAndView mav = new ModelAndView("problem_solve/c001_003.tiles");
		mav.addObject("list", list);
		
		return mav;
	}

	@Override
	@RequestMapping(value = "**/problem_page.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchProblem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String pro_num = request.getParameter("pro_num");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("pro_num", pro_num);
		List<Problem_VO> list1 = problem_Service.searchProblem(searchMap);
		List<ProblemExample_VO> list2 = problem_Service.searchExample(searchMap);
		ModelAndView mav = new ModelAndView("problem_solve/problem_page.tiles");
		mav.addObject("list1", list1);
		mav.addObject("list2", list2);
		
		return mav;
	}

	
	@Override
	@RequestMapping(value = "**/check_answer.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkAnswer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String answer = (String)request.getParameter("answer");
		String correct = "";
		Map<String, Object> searchMap = new HashMap<String, Object>();
		List<Problem_VO> list = problem_Service.searchProblem(searchMap);
		String proanswer = ((Problem_VO) list.get(0)).getPro_answer();
		if(answer.equals(proanswer)) correct = "정답입니다.";
		else correct = "틀렸습니다 다시한번 확인하세요.";
		
		ModelAndView mav = new ModelAndView("problem_solve/answer_page.tiles");
		mav.addObject("correct", correct);
		mav.addObject("list", list);
		return mav;
	}
	
	@Override
	@RequestMapping(value = "**/colselect_page.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView colSelectPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("category");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("category", category);
		List<ProCollection_VO> list = proCollection_Service.searchCategoryCollection(searchMap);
		
		ModelAndView mav = new ModelAndView("problem_solve/col_select_page.tiles");
		mav.addObject("list", list);
		return mav;
	}

	@Override
	@RequestMapping(value = "**/userColselect_page.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView userColSelectPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("category");
		return null;
	}

	@Override
	@RequestMapping(value = "**/collection_page.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView collectionPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String col_num = request.getParameter("number");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("col_num", col_num);
		
		List<Map<String, String>> list = problem_Service.selectProByCol(searchMap);
		ModelAndView mav = new ModelAndView("problem_solve/pro_collection_page.tiles");
		mav.addObject("list", list);
		System.out.println(list);
		return mav;
	}

	
	
	@Override
	public ModelAndView problemMakePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "**/makeProAjax001.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, String>> makeProAjax001(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String input = request.getParameter("string");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("category", input);
		
		List<Map<String, String>> list = problem_Service.selectCategory(searchMap);
		
		return list;
	}
	
	//카테고리 선택후 문제출제창으로
	@RequestMapping(value = "**/makePro001.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView makePro001(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, Object> searchMap = new HashMap<String, Object>();
		String categoryId = request.getParameter("category3").split("/")[0];
		String categoryName = request.getParameter("category3").split("/")[1];
		searchMap.put("category_id", categoryId);
		
		List<Map<String, String>> list = problem_Service.selectTag(searchMap);
		
		ModelAndView mav = new ModelAndView("problem_make/proMake_001Page.tiles");
		mav.addObject("categoryId",categoryId);
		mav.addObject("categoryName",categoryName);
		mav.addObject("list", list);
		return mav;
	}
	
	//문제 데이터 insert
	@RequestMapping(value = "**/makePro002.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView makePro002(@RequestParam HashMap<String, String> paramMap) throws Exception {
		
		String tag = paramMap.get("tag");
		paramMap.put("tag_id", tag.split("/")[0]);
		paramMap.put("tag_name", tag.split("/")[1]);
		problem_Service.insertProblem(paramMap);
		System.out.println(paramMap);
		return null;
	}
	
	

}