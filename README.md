# GmailSender

A Java App used to Send Email

# GmailSender

GmailSender is a simple Java project to send emails using Gmail SMTP.

---

## 1. Config File (`config.properties`)

Create a `config.properties` file in the root directory of your project. This file stores SMTP settings and sender credentials.

### Format:

# SMTP Server Settings

mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true

# Sender credentials

mail.username=your_email@gmail.com

# It's safer to use an environment variable or app password

# mail.password=your_app_password_here

mail.password=your_app_password_here

# (Optional) default recipient

mail.to=recipient_email@example.com
