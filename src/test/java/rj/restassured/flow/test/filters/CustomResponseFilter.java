package rj.restassured.flow.test.filters;
import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;

import io.restassured.response.Response;

import io.restassured.http.Header;

public class CustomResponseFilter implements Filter {


	@Override
	public Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec,
			io.restassured.specification.FilterableResponseSpecification responseSpec, FilterContext ctx) {
		// TODO Auto-generated method stub
		   Response originalResponse = ctx.next(requestSpec, responseSpec);

	        System.out.println("Custom Response Filter: Response status = " + originalResponse.getStatusCode());

	        // 🔹 Use ResponseBuilder to modify the response
	        ResponseBuilder responseBuilder = new ResponseBuilder().clone(originalResponse);
	        responseBuilder.setHeader("X-Custom-Header", "AddedInFilter"); // Add custom header
	       Response  modifiedResponse = responseBuilder.build();
	   
	        return modifiedResponse; // Return modified response
	}


}
