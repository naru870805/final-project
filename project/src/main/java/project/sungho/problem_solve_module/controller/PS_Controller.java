package project.sungho.problem_solve_module.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface PS_Controller {
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
