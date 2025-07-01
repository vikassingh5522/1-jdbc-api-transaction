package com.example.servlet;

import com.example.dao.AccountDAO;
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

@WebServlet("/accounts/transfer")
public class TransferServlet extends HttpServlet {
    private AccountDAO accountDAO;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
        templateEngine = new TemplateEngine();

        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(
                JakartaServletWebApplication.buildApplication(getServletContext())
        );
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");

        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebContext context = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response)
        );
        templateEngine.process("transfer", context, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebContext context = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response)
        );

        try {
            int fromAccountId = Integer.parseInt(request.getParameter("fromAccountId"));
            int toAccountId = Integer.parseInt(request.getParameter("toAccountId"));
            double amount = Double.parseDouble(request.getParameter("amount"));

            accountDAO.transferMoney(fromAccountId, toAccountId, amount);


            // ❌ ERROR HERE: Missing "/" before "accounts"
            // This will redirect to: http://localhost:8080/jdbc-transaction-web-appaccounts
            // Which causes a 404 Not Found error
            //    response.sendRedirect(request.getContextPath() + "accounts");
            // ✅ FIX: Use "/" before "accounts"
             response.sendRedirect(request.getContextPath() + "/accounts");

        } catch (SQLException | NumberFormatException e) {
            context.setVariable("error", "Transfer failed: " + e.getMessage());
            templateEngine.process("transfer", context, response.getWriter());
        }
    }
}
