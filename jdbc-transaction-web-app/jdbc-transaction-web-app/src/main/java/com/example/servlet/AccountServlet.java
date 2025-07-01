package com.example.servlet;

import com.example.dao.AccountDAO;
import com.example.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/accounts", "/accounts/create", "/accounts/update", "/accounts/delete"})
public class AccountServlet extends HttpServlet {
    private AccountDAO accountDAO;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
        templateEngine = new TemplateEngine();
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(
                JakartaServletWebApplication.buildApplication(getServletContext()));
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        WebContext context = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response)
        );

        try {
            switch (path) {
                case "/accounts":
                    List<Account> accounts = accountDAO.getAllAccounts();
                    context.setVariable("accounts", accounts);
                    templateEngine.process("list", context, response.getWriter());
                    break;

                case "/accounts/create":
                    templateEngine.process("create", context, response.getWriter());
                    break;

                case "/accounts/update":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Account account = accountDAO.getAccount(id);
                    context.setVariable("account", account);
                    templateEngine.process("update", context, response.getWriter());
                    break;

                case "/accounts/delete":
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    accountDAO.deleteAccount(deleteId);
                    response.sendRedirect(request.getContextPath() + "/accounts"); // ✅ Correct redirect
                    break;

                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if ("/accounts/create".equals(path)) {
                Account account = new Account();
                account.setAccountId(Integer.parseInt(request.getParameter("accountId")));
                account.setBalance(Double.parseDouble(request.getParameter("balance")));
                accountDAO.createAccount(account);
                response.sendRedirect(request.getContextPath() + "/accounts");

            } else if ("/accounts/update".equals(path)) {
                Account account = new Account();
                account.setAccountId(Integer.parseInt(request.getParameter("accountId")));
                account.setBalance(Double.parseDouble(request.getParameter("balance")));
                accountDAO.updateAccount(account);
                response.sendRedirect(request.getContextPath() + "/accounts");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
