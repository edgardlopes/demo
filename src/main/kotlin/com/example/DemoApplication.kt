package com.example

import org.apache.ibatis.annotations.Select
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.SecurityPrerequisite
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}

@Configuration
open class DemoSecurityConfig : WebSecurityConfigurerAdapter() {

    @ConfigurationProperties(prefix = "app.security")
    class DemoSecurityProperties : SecurityPrerequisite {
        val authorize: Map<String, Array<String>> = LinkedHashMap() // NOTE: order is important
        val authenticate: Map<String, Authenticate> = HashMap()

        class Authenticate {
            var name: String? = null
            var password: String? = null
            var role: Array<String> = arrayOf()
        }
    }

    @Bean
    open fun demoSecurityProperties(): DemoSecurityProperties {
        return DemoSecurityProperties()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        demoSecurityProperties().authorize.entries.forEach {
            http.authorizeRequests().antMatchers(it.key).hasAnyRole(*it.value)
        }
        http.authorizeRequests().and().httpBasic()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        demoSecurityProperties().authenticate.values.forEach {
            auth.inMemoryAuthentication().withUser(it.name).password(it.password).roles(*it.role)
        }
    }
}

@Controller
class HelloController @Autowired constructor(val service: RowService) {

    @RequestMapping("/{user}/rows", method = arrayOf(RequestMethod.GET))
    fun hello(@PathVariable user: String, model: Model): String {
        model.addAttribute("rows", service.getRows(user))
        return "${user}/hello"
    }
}

data class Row(
    val id: java.lang.Long,
    val name: java.lang.String,
    val address: java.lang.String,
    val email: java.lang.String,
    val phone: java.lang.String)

@Service
class RowService @Autowired constructor(val mapper: RowMapper, val mapperXml: RowMapperXml){
    fun getRows(user: String) : List<Row> {
        return if ("user1" == user) mapper.findAll() else  mapperXml.findAll();
    }
}

interface RowMapper {
    @Select("SELECT * FROM ROW")
    fun findAll(): List<Row>
}

interface RowMapperXml {
    fun findAll(): List<Row>
}
