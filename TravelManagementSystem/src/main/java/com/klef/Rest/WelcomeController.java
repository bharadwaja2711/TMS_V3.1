package com.klef.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WelcomeController {

    @Autowired
    private ReCaptchaValidationService validator;

    @Autowired
    private RegistrationRepository registrationRepository;

    @RequestMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registrationEntity", new RegistrationEntity());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationEntity") RegistrationEntity registrationEntity,
                           @RequestParam(name = "g-recaptcha-response") String captcha, Model model) {
        try {
            if (validator.validateCaptcha(captcha)) {
                registrationRepository.save(registrationEntity);
                model.addAttribute("registrationEntity", new RegistrationEntity());
                model.addAttribute("message", "Registration successful!!");
            } else {
                model.addAttribute("message", "Please verify captcha.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error occurred during registration.");
        }

        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginEntity", new LoginEntity());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginEntity loginEntity, Model model) {
        try {
            // Check credentials against data in the login table
            RegistrationEntity user = registrationRepository.findByUsernameAndPassword(loginEntity.getUsername(), loginEntity.getPassword());

            if (user != null) {
                // Successful login, redirect to the welcome page
                return "indexLogin";
            } else {
                // Incorrect credentials, show an error message
                model.addAttribute("message", "Incorrect username or password");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error occurred during login.");
        }

        return "login";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }
    
    @RequestMapping("/aboutLogin")
    public String aboutLogin()
    {
    	return "aboutLogin";
    }
    
    @RequestMapping("/indexLogin")
    public String indexLogin()
    {
    	return "indexLogin";
    }
    
    @RequestMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotEntity", new ForgotEntity());
        return "forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@ModelAttribute ForgotEntity forgotEntity, Model model) {
        try {
            // Check credentials against data in the registration table
            RegistrationEntity user = registrationRepository.findByUsernameAndSecurityQuestion(
                    forgotEntity.getUsername(), forgotEntity.getSecurityQuestion());

            if (user != null) {
                // Valid username and security question, you can implement password reset logic here
                // For now, we'll redirect to a success page
                return "indexLogin";
            } else {
                // Incorrect credentials, show an error message
                model.addAttribute("message", "Incorrect username or security question");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error occurred during password reset.");
        }

        return "forgot-password";
    }
    
}
