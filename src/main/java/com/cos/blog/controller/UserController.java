package com.cos.blog.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용 ( 인증이 필요 없는 곳)
// 그냥 주소가 /이면 index.jsp 허용
// static이하에 있는 /js/**, /css/**, /image/**  허용

@Controller
public class UserController {

	@GetMapping("/auth/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@GetMapping("/auth/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
	@GetMapping("/auth/kakao/callback")
	public @ResponseBody String kakaoCallback(String code) { // Data를 리턴해주는 컨트롤러 함수
		
		// POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
		// Retrofit2, OkHttp, RestTemplate   http 요청 라이브러리 
		// HttpsURLConnection
		
		RestTemplate rt = new RestTemplate();  
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type",  "application/x-www-form-urlencoded;charset=utf-8");
		
		// HttpBody 오브젝트 생성
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>() ;
		params.add("grant_type", "authorization_code");
		params.add("client_id", "77b69dac1495fde90e5e200de2abf38a");
		params.add("redirect_id", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code);		
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = 
				new HttpEntity<>(params,headers);
		System.out.println("http 요청시작");
		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답받음
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
		);
		System.out.println(response);
		System.out.println(response.getStatusCodeValue());
		System.out.println(response.getBody());
		System.out.println(response.getHeaders());
		
		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());
		
		//HttpHeader 오브젝트 생성
		headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest =  new HttpEntity<>(headers);
		System.out.println("카카오 프로필 요청시작");
		
		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답받음
				ResponseEntity<String> response2 = rt.exchange(
						"https://kapi.kakao.com/v2/user/me",
						HttpMethod.POST,
						kakaoProfileRequest,
						String.class
				);
		System.out.println(response2.getBody());
		KakaoProfile kakaoProfile = null;
		ObjectMapper objectMapper2 = new ObjectMapper();
		objectMapper2.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakaoAccount());
		
		return  response2.getBody();
	}

	
	
	
	
	@GetMapping("/user/updateForm")
	public String updateForm() {
		return "user/updateForm";
	}
}
