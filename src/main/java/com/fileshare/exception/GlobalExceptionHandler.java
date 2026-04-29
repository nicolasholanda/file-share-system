package com.fileshare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleFileNotFound(FileNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 404);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneric(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 500);
        mav.addObject("message", "An unexpected error occurred.");
        return mav;
    }
}
