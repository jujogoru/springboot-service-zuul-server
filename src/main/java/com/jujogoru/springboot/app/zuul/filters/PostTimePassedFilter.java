package com.jujogoru.springboot.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostTimePassedFilter extends ZuulFilter{
	
	private static Logger log = org.slf4j.LoggerFactory.getLogger(PostTimePassedFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		
		
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		log.info("Getting into a post filter");
		
		Long startTime = (Long) request.getAttribute("startTime");
		Long finalTime = System.currentTimeMillis();
		Long time = finalTime - startTime;
		
		log.info(String.format("It has been %s seconds", time.doubleValue()/1000));
		log.info(String.format("It has been %s ms", time));
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
