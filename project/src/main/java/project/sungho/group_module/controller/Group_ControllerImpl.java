package project.sungho.group_module.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.ibleaders.utility.ib_json.parser.JSONParser;

import project.sungho.group_module.service.Group_Service;
import project.sungho.group_module.service.Group_ServiceImpl;
import project.sungho.paging.Paging;
import project.sungho.post_module.service.Post_Service;

@Controller
public class Group_ControllerImpl {

	@Autowired
	Group_Service group_Service;
	@Autowired
	Post_Service post_Service;

	@RequestMapping(value = "group/groupmain", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView groupmain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("group/groupWholeList.tiles");
		mav.addObject("list", group_Service.selectWholeGroup());
		return mav;
	}

	@RequestMapping(value = "group/groupmaking.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView groupmaking(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("group/groupMake.tiles");
		return mav;
	}

	@RequestMapping(value = "group/make_newGroup.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView make_newGroup(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		group_Service.insertGroup(paramMap);
		ModelAndView mav = new ModelAndView("redirect:groupmain");
		return mav;
	}

	@RequestMapping(value = "group/mygroup.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView mygroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("group/groupMyList.tiles");
		mav.addObject("list", group_Service.selectMyGroup());
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "**/ajaxGroupSelect.pro", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> groupAjax(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return group_Service.selectOneGroup(paramMap);
	}

	@RequestMapping(value = "cafe/{key1}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView groupPage(@PathVariable("key1") String key1, @RequestParam HashMap<String, Object> paramMap,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			request.getSession().setAttribute("group_num", key1);
			paramMap.put("group_num", key1);
			if(!paramMap.containsKey("pageNum"))paramMap.put("pageNum", (String)"1");
			Map<String, Object> resultMap = group_Service.checkMemberState(paramMap);
			resultMap.putAll(group_Service.selectOneGroup(paramMap));
			ModelAndView mav = new ModelAndView("group/groupPage_main.tiles");
			mav.addObject("result", resultMap);
			mav.addObject("boardList", group_Service.selectGroupBoardList(paramMap));
			mav.addObject("replyList", post_Service.selectRecentReply(paramMap));
			mav.addObject("user", group_Service.checkMemberState(paramMap));
			if(paramMap.containsKey("need")) {
				mav.setViewName("group/forceSignUp.tiles");
			} else if (paramMap.containsKey("signUp")) {
				mav.setViewName("group/groupPage_signUp.tiles");
				mav.addObject("signUpResult",paramMap.get("signUp"));
			} else if (paramMap.containsKey("post_num")) {
				mav.addObject("post", post_Service.selectOneArticle(paramMap));
				mav.addObject("reply", post_Service.selectReplyList(paramMap));
				mav.setViewName("group/groupPage_article.tiles");
			} else if (paramMap.containsKey("board_num")) {
				List<Map<String, Object>> list = post_Service.selectArticleByBoard(paramMap);
				mav.addObject("postList", list);
				mav.addObject("board", paramMap);
				Paging page = new Paging(list.size(), 15, Integer.parseInt((String)paramMap.get("pageNum")));
				mav.addObject("page", page);
				mav.addObject("boardname", post_Service.selectBoardName(paramMap));
				mav.setViewName("group/groupPage_board.tiles");
			} else {
				List<Map<String, Object>> list = post_Service.selectArticleByGroup(paramMap);
				mav.addObject("postList", list);
				Paging page = new Paging(list.size(), 15, Integer.parseInt((String)paramMap.get("pageNum")));
				mav.addObject("page", page);
			}
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView(new RedirectView(request.getContextPath() + "/mainPage/mainPage001.do"));
		}
	}

	@RequestMapping(value = "cafe/managing.user", method = { RequestMethod.GET,RequestMethod.POST })
	public ModelAndView groupManaging(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		paramMap.put("group_num", request.getSession().getAttribute("group_num"));
		if (!group_Service.authorityCheck(paramMap, "manager")) return new ModelAndView(new RedirectView(request.getContextPath() + "/mainPage/mainPage001.do"));
		try {
			Map<String, Object> resultMap = group_Service.checkMemberState(paramMap);
			resultMap.putAll(group_Service.selectOneGroup(paramMap));
			ModelAndView mav = new ModelAndView("group/groupPage_managing.tiles");
			mav.addObject("result", resultMap);
			mav.addObject("memberList", group_Service.selectGroupMemberList(paramMap));
			mav.addObject("boardList", group_Service.selectGroupBoardList(paramMap));
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView(new RedirectView(request.getContextPath() + "/mainPage/mainPage001.do"));
		}

	}

	@RequestMapping(value = "cafe/write.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView write(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!group_Service.authorityCheck(paramMap, "user", "manager")) return new ModelAndView(new RedirectView("" +"auth?group_num="+paramMap.get("group_num"))); 
		Map<String, Object> resultMap = group_Service.checkMemberState(paramMap);
		resultMap.putAll(group_Service.selectOneGroup(paramMap));
		ModelAndView mav = new ModelAndView("group/groupPage_write.tiles");
		mav.addObject("result", resultMap);
		mav.addObject("boardList", group_Service.selectGroupBoardList(paramMap));
		return mav;
	}

	@RequestMapping(value = "cafe/board.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewGroupBoard(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!group_Service.authorityCheck(paramMap, "user", "manager")) return new ModelAndView(new RedirectView("" +"auth?group_num="+paramMap.get("group_num")));
		ModelAndView mav = new ModelAndView("redirect:" + paramMap.get("group_num"));
		mav.addObject("board_num", paramMap.get("board_num"));
		return mav;
	}

	@RequestMapping(value = "cafe/confirm.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView writeconfirm(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!group_Service.authorityCheck(paramMap, "user", "manager")) return new ModelAndView(new RedirectView("" +"auth?group_num="+paramMap.get("group_num")));
		group_Service.insertArticle(paramMap);
		ModelAndView mav = new ModelAndView(new RedirectView("" + paramMap.get("group_num")));
		mav.addObject("board_num", paramMap.get("board_num"));
		return mav;
	}

	@RequestMapping(value = "cafe/article.user", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewArticle(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!group_Service.authorityCheck(paramMap, "user", "manager")) return new ModelAndView(new RedirectView("" +"auth?group_num="+paramMap.get("group_num")));
		ModelAndView mav = new ModelAndView(new RedirectView("" + paramMap.get("group_num")));
		mav.addObject("board_num", paramMap.get("board_num"));
		mav.addObject("post_num", paramMap.get("post_num"));
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "cafe/add.user", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> addGroupBoard(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> inputMap = (new ObjectMapper()).readValue(request.getParameter("input"),
				new TypeReference<Map<String, String>>() {
				});
		return post_Service.insertBoard(inputMap);
	}

	@ResponseBody
	@RequestMapping(value = "cafe/delete.user", method = { RequestMethod.GET, RequestMethod.POST })
	public void delGroupBoard(String[] data, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		post_Service.deleteBoardList(data);
	}

	@ResponseBody
	@RequestMapping(value = "cafe/replyInsert.user", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> insertReply(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> inputMap = (new ObjectMapper()).readValue(request.getParameter("input"),
				new TypeReference<Map<String, String>>() {
				});
		return post_Service.insertReply(inputMap);
	}
	
	@ResponseBody
	@RequestMapping(value = "cafe/uploadImage", method = { RequestMethod.POST })
	public String uploadImage(MultipartHttpServletRequest mtfRequest, HttpServletRequest request) throws Exception {
		MultipartFile mf = mtfRequest.getFile("file");
		String root_path = request.getSession().getServletContext().getRealPath("/");
		String attach_path = "resources/image/";
		String path = root_path+attach_path;
		String originFileName = mf.getOriginalFilename(); // 원본 파일 명
		String fileName = System.currentTimeMillis() + originFileName;
		String safeFile = path + fileName;
		try {
			mf.transferTo(new File(safeFile));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}

	@RequestMapping(value = "cafe/yield", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView yieldManager(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		group_Service.yieldManager(paramMap);
		ModelAndView mav = new ModelAndView(
				new RedirectView(request.getContextPath() + "/cafe/" + paramMap.get("num")));
		return mav;
	}

	@RequestMapping(value = "cafe/deport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView deportUser(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		group_Service.deportUser(paramMap);
		ModelAndView mav = new ModelAndView(new RedirectView(request.getContextPath() + "/cafe/managing.user"));
		mav.addObject("group_num", paramMap.get("group_num"));
		return mav;
	}

	@RequestMapping(value = "cafe/apply", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView applyUser(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (paramMap.get("apply").equals("true")) {
			group_Service.applyUser(paramMap);
		} else {
			group_Service.deportUser(paramMap);
		}
		ModelAndView mav = new ModelAndView(new RedirectView(request.getContextPath() + "/cafe/managing.user"));
		mav.addObject("group_num", paramMap.get("group_num"));
		 
		return mav;
	}
	
	@RequestMapping(value = "cafe/auth", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView cafeAuth(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(new RedirectView("" + paramMap.get("group_num")));
		mav.addObject("need","");
		return mav;
		
	}
	
	@RequestMapping(value = "cafe/signUp", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView cafeSignUp(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(new RedirectView("" + paramMap.get("group_num")));
		mav.addObject("signUp",group_Service.signUpCafe(paramMap));
		return mav;
	}
	
	@RequestMapping(value = "cafe/update", method = { RequestMethod.POST })
	public ModelAndView cafeUpdate(@RequestParam HashMap<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		group_Service.updateGroup(paramMap);
		return new ModelAndView(new RedirectView(request.getContextPath() + "/cafe/managing.user"));
	}

}
