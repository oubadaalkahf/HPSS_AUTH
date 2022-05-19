package org.sid.hpslogin.registration;

import lombok.AllArgsConstructor;

import org.sid.hpslogin.appuser.AppUserRepository;
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
    

}
