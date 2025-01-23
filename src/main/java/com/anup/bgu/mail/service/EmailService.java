package com.anup.bgu.mail.service;

import com.anup.bgu.mail.dto.MailData;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Map;

public interface EmailService extends MessageListener {

}
