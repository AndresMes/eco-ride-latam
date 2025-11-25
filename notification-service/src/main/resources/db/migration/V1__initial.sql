CREATE TABLE notification_template (
   id SERIAL PRIMARY KEY,
   code VARCHAR(50) NOT NULL UNIQUE,
   channel VARCHAR(20) NOT NULL, -- email | sms | push
   subject VARCHAR(255),
   body TEXT
);
