package com.example.demo;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.User;
import com.example.demo.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;


    @RequestMapping(value= {"/", "/login"})
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        return model;
    }

    @RequestMapping("/signup")
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("user/signup");
        return model;
    }

    @PostMapping("/signup")
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());

        if(userExists != null) {
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if(bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            userService.saveUser(user);
            model.addObject("msg", "User has been registered successfully!");
            model.addObject("user", new User());
            model.setViewName("user/signup");
        }

        return model;
    }

    @RequestMapping("/home/home")
    public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/home");
        return model;
    }

    @RequestMapping("/access_denied")
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }
    @PostMapping("/processwithdrawal")
    public String withdrawform(Model model, @Valid Account account,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "withdrawalform";
        }
        AccountRepository.save(account);
        return "redirect:/accountlist";
    }

    @PostMapping("/processdeposit")
    public String depositform(Model model, @Valid Account account,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "depositform";
        }
        AccountRepository.save(account);
        return "redirect:/accountlist";
    }

    @RequestMapping("/accountlist")

    public String listaccount(Model model) {


        model.addAttribute("user", UserRepository.findAllById(id).get() );
        model.addAttribute("accounts", AccountRepository.findAll());
        return "accountlist";
    }
}
