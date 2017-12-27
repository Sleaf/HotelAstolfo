package com.enihsyou.astolfo.hotel.service

import com.enihsyou.astolfo.hotel.domain.Guest
import com.enihsyou.astolfo.hotel.domain.Transaction
import com.enihsyou.astolfo.hotel.domain.User
import com.enihsyou.astolfo.hotel.exception.注册时用户已存在
import com.enihsyou.astolfo.hotel.exception.用户不存在
import com.enihsyou.astolfo.hotel.exception.用户名和密码不匹配
import com.enihsyou.astolfo.hotel.exception.用户未绑定身份证
import com.enihsyou.astolfo.hotel.exception.相同身份证已存在
import com.enihsyou.astolfo.hotel.exception.身份证不存在
import com.enihsyou.astolfo.hotel.repository.GuestRepository
import com.enihsyou.astolfo.hotel.repository.TransactionRepository
import com.enihsyou.astolfo.hotel.repository.UserRepository
import com.google.common.hash.Hashing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets


interface UserService {

    fun login(phoneNumber: String, password: String): User

    fun listUsers(): List<User>
    fun createUser(phoneNumber: String, password: String, nickname: String = "", role: User.UserRole): User
    fun getUser(phone: String): User
    fun updateUser(phone: String, user: User): User
    fun deleteUser(phone: String)

    fun listTransactions(phone: String): List<Transaction>

    fun listGuests(phone: String): List<Guest>
    fun getGuest(identification: String): Guest
    fun getGuest(phone: String, identification: String): Guest
    fun addGuest(phone: String, guest: Guest): ResponseEntity<Guest>
    fun deleteGuest(guest: Guest)
    fun modifyGuest(identification: String, payload: Map<String, String>): Guest
}

@Service(value = "用户层逻辑")
class UserServiceImpl : UserService {

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var guestRepository: GuestRepository
    @Autowired lateinit var transactionRepository: TransactionRepository

    override fun getGuest(identification: String): Guest
        = guestRepository.findByIdentification(identification) ?:
        throw 身份证不存在(identification)

    override fun getGuest(phone: String, identification: String): Guest
        = getUser(phone).guests
        .find { it.identification == identification } ?:
        throw 用户未绑定身份证(phone, identification)

    override fun listUsers(): List<User>
        = userRepository.findAll().toList()

    override fun deleteUser(phone: String)
        = userRepository.delete(getUser(phone))

    override fun modifyGuest(identification: String, payload: Map<String, String>): Guest {
        val guest = getGuest(identification)

        payload["name"]?.let { guest.name = it }
        return guest
    }

    override fun deleteGuest(guest: Guest)
        = guestRepository.delete(guest)

    override fun getUser(phone: String): User
        = existUser(phone)

    override fun updateUser(phone: String, user: User): User {
        val old_user = getUser(phone)
        if (user.password.isNotEmpty())
            old_user.password = getCheckedPassword(user.password)
        if (user.nickname.isNotEmpty())
            old_user.nickname = user.nickname
        userRepository.save(old_user)
        return old_user
    }

    private fun existUser(phone: String): User =
        userRepository.findByPhoneNumber(phone) ?:
            throw throw 用户不存在(phone)

    override fun listTransactions(phone: String)
        = existUser(phone).let { transactionRepository.findByUser(it).toList() }


    override fun addGuest(phone: String, guest: Guest): ResponseEntity<Guest> {
        val iden = guest.identification
        val user = existUser(phone)

        if (guestRepository.findByIdentification(iden) == null) {
            val new_guest = Guest(identification = iden, name = guest.name, user = mutableListOf())
            user.guests.add(new_guest)
            new_guest.user.add(user)

            guestRepository.save(new_guest)
            userRepository.save(user)
            return ResponseEntity(HttpStatus.CREATED)
        } else
            throw 相同身份证已存在(iden)
    }

    override fun listGuests(phone: String): List<Guest> {
        existUser(phone).let {
            return guestRepository.findByUserId(it.id).toList()
        }
    }

    private fun getCheckedPassword(password: String): String {
        return if (password.length != 64)
            Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
        else
            password
    }

    override fun createUser(
        phoneNumber: String,
        password: String,
        nickname: String,
        role: User.UserRole
    ): User {
        /*注册并返回*/
        if (userRepository.findByPhoneNumber(phoneNumber) != null)
            throw 注册时用户已存在(phoneNumber)
        val user = User(
            phoneNumber = phoneNumber,
            nickname = nickname,
            /*如果密码不是经过前端哈希的，这里进行哈希*/
            password = getCheckedPassword(password),
            role = role
        )
        userRepository.save(user)
        return user
    }

    override fun login(
        phoneNumber: String,
        password: String
    ): User {
        val user = existUser(phoneNumber)
        if (user.password == getCheckedPassword(password)) {
            return user
        }
        throw 用户名和密码不匹配()
    }
}



