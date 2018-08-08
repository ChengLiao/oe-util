package com.oe.util.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author 928
 * @date 2016-8-9
 */
public abstract class TransferService {
	protected NamedParameterJdbcTemplate jdbcTemplateOra;
	protected NamedParameterJdbcTemplate jdbcTemplateTd;
	protected List<String> selSqls = new ArrayList<String>();
	protected List<String> insSqls = new ArrayList<String>();
	protected int pageSize;
	
	public void setJdbcTemplateOra(NamedParameterJdbcTemplate jdbcTemplateOra) {
		this.jdbcTemplateOra = jdbcTemplateOra;
	}
	
	public void setJdbcTemplateTd(NamedParameterJdbcTemplate jdbcTemplateTd) {
		this.jdbcTemplateTd = jdbcTemplateTd;
	}
	
	public void setSelSqls(List<String> selSqls) {
		this.selSqls = selSqls;
	}
	
	public void setInsSqls(List<String> insSqls) {
		this.insSqls = insSqls;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public abstract int transfer();
}
