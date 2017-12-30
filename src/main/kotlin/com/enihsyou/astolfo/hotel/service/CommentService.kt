package com.enihsyou.astolfo.hotel.service

import com.enihsyou.astolfo.hotel.domain.Comment
import com.enihsyou.astolfo.hotel.exception.评论不存在
import com.enihsyou.astolfo.hotel.exception.评论已存在不可修改
import com.enihsyou.astolfo.hotel.repository.CommentRepository
import com.enihsyou.astolfo.hotel.repository.GuestRepository
import com.enihsyou.astolfo.hotel.repository.RoomDirectionRepository
import com.enihsyou.astolfo.hotel.repository.RoomRepository
import com.enihsyou.astolfo.hotel.repository.RoomTypeRepository
import com.enihsyou.astolfo.hotel.repository.TransactionRepository
import com.enihsyou.astolfo.hotel.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


interface CommentService {
    fun createComment(transaction: Int, comment: Comment)
    fun showTransactionComment(transaction: Int): Comment
    fun listRoomComment(room: Int): List<Map<String, Any>>
}

@Service(value = "评论层逻辑")
class CommentServiceImpl : CommentService {

    override fun showTransactionComment(transaction: Int): Comment {
        val transaction1 = transactionRepository.findOne(transaction)
        if (commentRepository.exists(transaction1.commentId))
            return commentRepository.findOne(transaction1.commentId)
        else
            throw 评论不存在(transaction)
    }

    override fun listRoomComment(room: Int): List<Map<String, Any>> {
        val findOne = roomRepository.findOne(room)
        val comments = mutableListOf<Map<String, Any>>()
        findOne.comments.map {
            val usr = userRepository.findOne(it.userId)
            mutableMapOf(
                "id" to it.id,
                "body" to it.body,
                "user" to mapOf(
                    "id" to usr.id,
                    "nickname" to usr.nickname,
                    "role" to usr.role.name
                ),
                "createDate" to it.createdDate
            )
        }.forEach { comments += it }
        return comments.toList()
    }

    override fun createComment(transaction: Int, comment: Comment) {
        /*先找到订单*/
        val findOne = transactionRepository.findOne(transaction)
        if (findOne.commentId != null)
            throw 评论已存在不可修改(transaction)

        /*设置评论的创建者*/
        comment.userId = findOne.user.id

        /*保存评论*/
        val save = commentRepository.save(comment)

        /*修改关系*/
        findOne.commentId = save.id
        findOne.room.comments.add(save)
        transactionRepository.save(findOne)
        roomRepository.save(findOne.room)
    }

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var commentRepository: CommentRepository
    @Autowired lateinit var transactionRepository: TransactionRepository
    @Autowired lateinit var roomRepository: RoomRepository
    @Autowired lateinit var roomTypeRepository: RoomTypeRepository
    @Autowired lateinit var roomDirectionRepository: RoomDirectionRepository
    @Autowired lateinit var guestRepository: GuestRepository
}
