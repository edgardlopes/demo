package com.example

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}

@Controller
class HelloController {
    @Autowired
    private val service: RowService? = null

    @RequestMapping(path = arrayOf("/rows"), method = arrayOf(RequestMethod.GET))
    fun hello(model: Model): String {
        model.addAttribute("rows", service!!.getRows())
        return "hello"
    }
}

data class Row(val id: Long, val name: String)

@Service
class RowService {
    fun getRows() : List<Row>? {
        return listOf(Row(1L, "Foo"));
    }
}

