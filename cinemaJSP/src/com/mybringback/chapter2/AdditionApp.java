package com.mybringback.chapter2;

import java.io.IOException;

import java.io.PrintWriter;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

public class AdditionApp extends HttpServlet {

@Override

protected void doPost(HttpServletRequest req, HttpServletResponse resp)

throws ServletException, IOException {

// TODO Auto-generated method stub

PrintWriter out = resp.getWriter();

resp.setContentType("text/html");

String name = req.getParameter("param1");

int num1 =Integer.parseInt(req.getParameter("param2"));

int num2 =Integer.parseInt(req.getParameter("param3"));

int result = num1+num2;

out.println("");out.println("");

out.println("Your name is"+name+"
");

out.println("Tha addition of two numbers is"+result);

out.println("");out.println("");

}

}
