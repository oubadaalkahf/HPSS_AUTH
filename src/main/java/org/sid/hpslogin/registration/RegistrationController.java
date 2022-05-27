package org.sid.hpslogin.registration;

import lombok.AllArgsConstructor;

import java.util.List;

import org.sid.hpslogin.appuser.AppUser;
import org.sid.hpslogin.appuser.AppUserRepository;
import org.sid.hpslogin.appuser.AppUserService;
import org.sid.hpslogin.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController

@RequestMapping(path = "registration")

@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;
    
    @PostMapping
    public ResponseEntity<Object> register(@RequestBody RegistrationRequest request) {
    	if(appUserRepository.findByEmail(request.getEmail()).isPresent())
    	{
            return ResponseHandler.generateResponse("Email Already Exists", HttpStatus.ACCEPTED,null);
    	}
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
    
    @PostMapping(path = "fcm_token")
    public ResponseEntity<Object> fcm(@RequestParam("device_token") String fcmToken,@RequestParam("user_email") String email){
	AppUser user=	appUserRepository.findByEmail(email).get();
    	return appUserService.addToken(fcmToken,user);
    	
    }
  
    @GetMapping(path = "allUsers")
    public ResponseEntity<Object> allusers(){
    	return appUserService.getUsers();
    }
    
}
