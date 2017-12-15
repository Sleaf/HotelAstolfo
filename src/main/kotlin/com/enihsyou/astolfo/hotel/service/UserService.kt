package com.enihsyou.astolfo.hotel.service

import com.enihsyou.astolfo.hotel.domain.User
import com.enihsyou.astolfo.hotel.error.注册时用户已存在
import com.enihsyou.astolfo.hotel.error.用户不存在
import com.enihsyou.astolfo.hotel.repository.UserRepository
import com.google.common.hash.Hashing
import org.springframework.stereotype.Service
import org.springframework.data.domain.Pageable
import java.nio.charset.StandardCharsets
import javax.annotation.Resource

interface UserService {
    fun signUp(phoneNumber: String, password: String, nickname: String = "")
    fun login(phoneNumber: String, password: String)
    fun findUserByPhone(phone: String): User?
    fun listUsers(pageable: Pageable): List<User>
    fun updateInformation(user:User)
    fun deleteUser(phone: String)
}

@Service
class UserServiceImpl : UserService {
    override fun updateInformation(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteUser(phone: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Resource
    lateinit var repository: UserRepository

    override fun findUserByPhone(phone: String): User? {
        return repository.findOne(phone)
    }

    override fun listUsers(pageable: Pageable): List<User> {
        return repository.findAll(pageable).toList()
    }

    override fun signUp(phoneNumber: String, password: String, nickname: String) {
        val user = repository.findOne(phoneNumber)
        /*如果用户已经存在*/
        if (user == null) {
            var password_checked = password
            /*如果密码不是经过前端哈希的，这里进行哈希*/
            if (password.length != 64)
                password_checked = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
            /*注册并返回*/
            repository.save(User(
                    phone_number = phoneNumber,
                    nickname = nickname,
                    password = password_checked
                    ))
        } else throw 注册时用户已存在(phoneNumber)
    }

    override fun login(phoneNumber: String, password: String) {
        val user = repository.findOne(phoneNumber) ?: throw 用户不存在(phoneNumber)
        return
    }

}