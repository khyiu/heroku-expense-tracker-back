package be.kuritsu.hetb.security;

import java.text.MessageFormat;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.exception.AccessDeniedException;

@Aspect
@Component
public class SecurityAspect {

    @AfterReturning(value = "@annotation(SecuritySubject)", returning = "expense")
    public void logBeforeMethodCall(Expense expense) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expense.getOwner().equals(currentUserName)) {
            throw new AccessDeniedException(MessageFormat.format("Current user [{0}] cannot access expense [{1}]", currentUserName, expense.getId()));
        }
    }
}