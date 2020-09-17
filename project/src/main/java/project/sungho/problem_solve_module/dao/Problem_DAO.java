package project.sungho.problem_solve_module.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import project.sungho.problem_solve_module.vo.Problem_VO;

public interface Problem_DAO {
	
	public List<Problem_VO> searchList(Map<String, Object> searchMap) throws DataAccessException;

}
