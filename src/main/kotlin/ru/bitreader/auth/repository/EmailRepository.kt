package ru.bitreader.auth.repository

import io.quarkus.mailer.Mail
import javax.inject.Inject
import io.quarkus.mailer.Mailer
import ru.bitreader.auth.models.database.UserModel
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EmailRepository {

    @Inject
    private lateinit var mailer: Mailer

    fun sendWelcome(user: UserModel) {
        val mail = Mail.withHtml(user.email, "Добро пожаловать", "Спасибо что выбрали нас, ${user.username}!")
        mailer.send(mail)
    }

    fun restore(email: String) {
        val mail = Mail.withHtml(email, "Восстановление аккаунта", "Следуйте инструкции ниже. (В разработке)")
        mailer.send(mail)
    }

}