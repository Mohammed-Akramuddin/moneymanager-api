package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.dto.ExpenseDTO;
import com.atg.moneymanager.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private  EmailService emailService;
    @Autowired
    private ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

//    @Scheduled(cron = "0 * * * * *", zone="IST")
    @Scheduled(cron = "0 0 22 * * *", zone="IST")
    public void sendDailyIncomeExpenseReminder(){
        log.info("job started sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles=profileRepository.findAll();
        for(ProfileEntity profile:profiles) {
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    +"This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                    +"<a href=" + frontendUrl + " style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>Go to Money Manager</a>"
                    +"<br><br>Best regards,<br>Money Manager Team";
            emailService.sendMail(profile.getEmail(),"Daily reminder: Add your income and expenses",body);
        }
        log.info("job completed sendDailyIncomeExpenseReminder()");

    }

//    @Scheduled(cron = "0 * * * * *", zone="IST")
    @Scheduled(cron = "0 0 23 * * *", zone="IST")
    public void sendDailyExpenseSummary(){
        log.info("Job started sendDailyExpenseSummary()");
        List<ProfileEntity> profiles=profileRepository.findAll();
        for (ProfileEntity profile:profiles){
            List<ExpenseDTO>ans=expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if(!ans.isEmpty()){
                StringBuilder table=new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th></tr>");
                int i=0;
                for (ExpenseDTO expense : ans) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getCategory_id() != null ? expense.getCategoryName(): "NA")
                            .append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body="Hi " + profile.getFullName() + ",<br><br>Here is the summary of your expenses for today: <br/><br/>"+table+"<br/><br/>Money Manager Team";
                emailService.sendMail(profile.getEmail(),"Your daily expense summary",body);
            }
        }
        log.info("Job completed sendDailyExpenseSummary()");

    }
}
