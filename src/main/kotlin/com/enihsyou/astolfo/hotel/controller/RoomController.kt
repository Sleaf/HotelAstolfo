package com.enihsyou.astolfo.hotel.controller

import com.enihsyou.astolfo.hotel.domain.Room
import com.enihsyou.astolfo.hotel.domain.RoomDirection
import com.enihsyou.astolfo.hotel.domain.RoomType
import com.enihsyou.astolfo.hotel.repository.RoomDirectionRepository
import com.enihsyou.astolfo.hotel.repository.RoomTypeRepository
import com.enihsyou.astolfo.hotel.service.RoomService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@RestController("房间接口控制器")
@RequestMapping("/api/rooms")
class RoomController {

    @Autowired lateinit var roomService: RoomService
    @Autowired lateinit var roomTypeRepository: RoomTypeRepository
    @Autowired lateinit var roomDirectionRepository: RoomDirectionRepository


    /*根据条件搜索房间*/
    @GetMapping("/list")
    fun listRoom(
        @RequestParam("from", required = false) from: LocalDateTime? = null,
        @RequestParam("to", required = false) to: LocalDateTime? = null,
        @RequestParam("type", required = false) type: String? = null,
        @RequestParam("direction", required = false) direction: String? = null,
        @RequestParam("priceFrom", required = false) priceFrom: Int? = null,
        @RequestParam("priceTo", required = false) priceTo: Int? = null,
        @RequestParam("floor", required = false) floor: Int? = null,
        @RequestParam("number", required = false) number: Int? = null): List<Room> {
        return roomService.listRoomByParameter(from, to, type, direction, priceFrom, priceTo, floor, number)
    }


    /*管理员添加一个房间*/
    @PostMapping("/add")
    fun addRoom(@RequestBody room: Room) {
        roomService.addRoom(room)
    }


    /*添加房间类型定义*/
    @PostMapping("/addType")
    fun addType(@RequestBody type: RoomType) {
        roomService.addType(type)
    }


    /*添加房间朝向定义*/
    @PostMapping("/addDirection")
    fun addDirection(@RequestBody direction: RoomDirection) {
        roomService.addRoomDirection(direction)
    }


    /*填充数据*/
    @GetMapping("/dummy")
    fun dummy() {
        val type = roomTypeRepository.findByType("大床房")!!

        val direction = roomDirectionRepository.findByType("东")!!

        addRoom(Room(roomNumber = Room.RoomNumber(1, 1), type = type, direction = direction, specialty = "141234", price = 101))
        addRoom(Room(roomNumber = Room.RoomNumber(1, 2), type = type, direction = direction, specialty = "efw", price = 99))
        addRoom(Room(roomNumber = Room.RoomNumber(1, 3), type = type, direction = direction, specialty = "kutuiyr", price = 100))
    }

    @GetMapping("/dummyD")
    fun dummyDirection() {
        addDirection(RoomDirection(type = "东", description = "面朝大海"))
        addDirection(RoomDirection(type = "南", description = "春暖花开"))
    }

    @GetMapping("/dummyT")
    fun dummyType() {
        addType(RoomType(type = "大床房", description = "EMMC"))
        addType(RoomType(type = "大床房2", description = "EMMC"))
        addType(RoomType(type = "大床房3", description = "EMMC"))
    }
}

