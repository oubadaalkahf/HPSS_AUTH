package org.sid.hpslogin.security.Filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import org.sid.hpslogin.appuser.AppUser;
import org.sid.hpslogin.appuser.AppUserRepository;
import org.sid.hpslogin.registration.RegistrationService;
import org.sid.hpslogin.responseHandler.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Dynamic;

import lombok.AllArgsConstructor;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	
	
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}
	 

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
	
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(email, password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		AppUser appUser = (AppUser) authResult.getPrincipal();
		Algorithm algorithm = Algorithm.HMAC256("hps-secret-123*$");
		String jwtAccess = JWT.create()
			      .withSubject(appUser.getFirstName())
			      .withExpiresAt(new Date(System.currentTimeMillis()+5*60*1000))
			      .withIssuer(request.getRequestURL().toString())
			      .withClaim("roles", appUser.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
			      .sign(algorithm);
	
		Map<String,Object> resp = new HashMap<>(); 
		 response.setContentType("application/json");
		response.setHeader("authorization", jwtAccess);
		resp.put("data", appUser);
		resp.put("status",HttpStatus.ACCEPTED);
		resp.put("message", "Login Successful");
		new ObjectMapper().writeValue(response.getOutputStream(), resp);
		
		}
	

	
	
	

}
