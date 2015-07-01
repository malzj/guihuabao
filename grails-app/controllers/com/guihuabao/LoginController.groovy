package com.guihuabao

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

import org.springframework.web.multipart.MultipartFile

class LoginController {
    def login(){
        def username = params.username
        def password = params.password
        def userInstance = User.findByUsernameAndPassword(username,password)
        if (!userInstance){
            redirect(action:'relogin')
            return
        }
        redirect(action:'userList')
    }
    def index() {}
    def userList(Integer max){
        params.max = Math.min(max ?: 10, 100)
        [userInstanceList: User.list(params), userInstanceTotal: User.count()]
    }
    def relogin(){

    }
    def userCreate() {

        [userInstance: new User(params),companyList: Company.list()]
    }
    def userSave() {
        def userInstance = new User(params)
        def companyUser = new CompanyUser(params)
        if (!userInstance.save(flush: true)||!companyUser.save(flush: true)) {
            render(view: "userCreate", model: [userInstance: userInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "userShow", id: userInstance.id)
    }
    def userShow(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userList")
            return
        }

        [userInstance: userInstance]
    }
    def userEdit(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userList")
            return
        }

        [userInstance: userInstance]
    }
    def userUpdate(Long id, Long version) {
        def userInstance = User.get(id)

        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userList")
            return
        }

        if (version != null) {
            if (userInstance.version > version) {
                userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'user.label', default: 'User')] as Object[],
                        "Another user has updated this User while you were editing")
                render(view: "userEdit", model: [userInstance: userInstance])
                return
            }
        }

        userInstance.properties = params

        if (!userInstance.save(flush: true)) {
            render(view: "userEdit", model: [userInstance: userInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "userShow", id: userInstance.id)
    }
    def userDelete(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userList")
            return
        }

        try {
            userInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userList")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "userShow", id: id)
        }
    }

    def companyList(Integer max){
        params.max = Math.min(max ?: 10, 100)
        [companyInstanceList:Company.list(params), companyInstanceTotal: Company.count()]
    }
    def companyCreate(){
        [companyInstance: new Company(params)]
    }
    def companySave(){
        def companyInstance = new Company(params)
        def  filePath
        def    fileName

        MultipartFile f = request.getFile('file1')
        if(!f.empty) {
            fileName=f.getOriginalFilename()
            filePath="web-app/images/"
            f.transferTo(new File(filePath+fileName))
        }


        companyInstance.logoimg=fileName
        if(!companyInstance.save(flush: true)){
            render(view: "companyCreate",model: [companyInstance: companyInstance])
        }

        flash.message =message(code: 'default.created.message', args: [message(code: 'company.label', default: 'Company'), companyInstance.id])
        redirect(action: "companyShow", id:companyInstance.id)
    }
    def companyShow(Long id){
        def companyInstance = Company.get(id)
        if(!companyInstance){
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "companyList")
            return
        }
        [companyInstance: companyInstance]
    }
    def companyEdit(Long id){
        def companyInstance = Company.get(id)

        if(!companyInstance){
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "companyList")
            return
        }
        [companyInstance: companyInstance]
    }
    def companyUpdate(Long id, Long version) {
        def companyInstance = Company.get(id)
        def  filePath
        def    fileName

        MultipartFile f = request.getFile('file1')
        if(!f.empty) {
            fileName=f.getOriginalFilename()
            filePath="web-app/images/"
            f.transferTo(new File(filePath+fileName))
        }


        companyInstance.logoimg=fileName
        if (!companyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "companyList")
            return
        }

        if (version != null) {
            if (companyInstance.version > version) {
                companyInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "companyEdit", model: [companyInstance: companyInstance])
                return
            }
        }

        companyInstance.properties = params

        if (!companyInstance.save(flush: true)) {
            render(view: "companyEdit", model: [companyInstance: companyInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), companyInstance.id])
        redirect(action: "companyShow", id: companyInstance.id)
    }
    def companyDelete(Long id){
        def companyInstance = Company.get(id)
        if(!companyInstance){
            flash.message = message(code: 'default.not.found.message', args: [message(code:  'user.label', default: 'User'), id])
            redirect(action: "companyList")
            return
        }

        try{
            companyInstance.delete(flush: true)
            redirect(action: "companyList")
        }
        catch(DataIntegrityViolationException e) {
            redirect(action: "companyShow", id: id)
        }
    }

    def roleList(Integer max){
        params.max = Math.min(max ?: 10, 100)
        [roleInstanceList:Role.list(params), roleInstanceTotal: Role.count()]
    }
    def roleCreate(){
        [roleInstance: new Role(params)]
    }
    def roleSave(){
        def roleInstance = new Role(params)
        if (!roleInstance.save(flush: true)) {
            render(view: "roleCreate", model: [roleInstance: roleInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])
        redirect(action: "roleShow", id: roleInstance.id)
    }
    def roleShow(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleList")
            return
        }

        [roleInstance: roleInstance]
    }
    def roleEdit(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleList")
            return
        }

        [roleInstance: roleInstance]
    }
    def roleUpdate(Long id, Long version) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleList")
            return
        }

        if (version != null) {
            if (roleInstance.version > version) {
                roleInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'role.label', default: 'Role')] as Object[],
                        "Another role has updated this Role while you were editing")
                render(view: "roleEdit", model: [roleInstance: roleInstance])
                return
            }
        }

        roleInstance.properties = params

        if (!roleInstance.save(flush: true)) {
            render(view: "roleEdit", model: [roleInstance: roleInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'role.label', default: 'Role'), roleInstance.id])
        redirect(action: "roleShow", id: roleInstance.id)
    }
    def roleDelete(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleList")
            return
        }

        try {
            roleInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleList")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'role.label', default: 'Role'), id])
            redirect(action: "roleShow", id: id)
        }
    }

    def hxset(){

    }
    //功能介绍
    def funIntroduction(Long id){
            def funIntroduction = FunIntroduction.get(id)
            if (!funIntroduction) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
                redirect(action: "list")
                return
            }
            [funIntroduction: funIntroduction]
        }


    def funIntroductionSave(Long id, Long version){
        def s=params
        def ss=params.content
        def funIntroduction = FunIntroduction.get(id)
        if (!funIntroduction) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (funIntroduction.version > version) {
                funIntroduction.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "edit", model: [funIntroduction: funIntroduction])
                return
            }
        }

        funIntroduction.properties = params


        if (!funIntroduction.save(flush: true)) {
            render(view: "edit", model: [funIntroduction: funIntroduction])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), funIntroduction.id])
        redirect(action: "funIntroduction", id: funIntroduction.id)

    }
    //反馈
    def feedback(Integer max){

            params.max = Math.min(max ?: 10, 100)
            [feedbackInstanceList: Feedback.list(params), feedbackInstanceTotal: Feedback.count()]

    }
    //登录图片
    def loginImg(Long id){
        def loginImg= IndexImg.get(id)
        [loginImg:loginImg]


    }
    def loginImgSave(Long id, Long version){
        def loginimg = IndexImg.get(id)
        def  filePath
        def    fileName

        MultipartFile f = request.getFile('file1')
        if(!f.empty) {
            fileName=f.getOriginalFilename()
            filePath="web-app/images/"
            f.transferTo(new File(filePath+fileName))
            loginimg.img=fileName
        }



        if (!loginimg) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "loginImg", id: loginimg.id)
            return
        }

        if (version != null) {
            if (loginimg.version > version) {
                loginimg.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "companyEdit", model: [companyInstance: loginimg])
                return
            }
        }

        loginimg.properties = params

        if (!loginimg.save(flush: true)) {
            render(view: "companyEdit", model: [companyInstance: loginimg])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), loginimg.id])
        redirect(action: "loginImg", id: loginimg.id)


    }
    //系统通知
    def inform(Long id){
        def inform = Inform.get(id)
        if (!inform) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
//            redirect(action: "list")
            return
        }
        [inform: inform]

    }
    def informSave(Long id, Long version){
        def inform = Inform.get(id)
        if (!inform) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (inform.version > version) {
                inform.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "edit", model: [funIntroduction: inform])
                return
            }
        }

        inform.properties = params


        if (!inform.save(flush: true)) {
            render(view: "edit", model: [funIntroduction: inform])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), inform.id])
        redirect(action: "loginImg", id: inform.id)

    }
    //版本更新
    def version(Long id){
        def banben = Banben.get(id)
        if (!banben) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
//            redirect(action: "list")
            return
        }
        [banben: banben]

    }
    def banbenSave(Long id, Long version){
        def banben = Banben.get(id)
        if (!banben) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (banben.version > version) {
                banben.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "edit", model: [funIntroduction: banben])
                return
            }
        }

        banben.properties = params


        if (!banben.save(flush: true)) {
            render(view: "edit", model: [funIntroduction: banben])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), inform.id])
        redirect(action: "version", id: banben.id)


    }
    //使用条款
    def clause(Long id){
        def clause = Clause.get(id)
        if (!clause) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
//            redirect(action: "list")
            return
        }
        [clause: clause]

    }
    def clauseSave(Long id, Long version){
        def clause = Clause.get(id)
        if (!clause) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (clause.version > version) {
                clause.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "edit", model: [funIntroduction: clause])
                return
            }
        }

        clause.properties = params


        if (!clause.save(flush: true)) {
            render(view: "edit", model: [funIntroduction: clause])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), clause.id])
        redirect(action: "clause", id: clause.id)

    }

    //和许助手
    def hxhelper(){

    }
    def bookCreate(){
        [bookInstance: new Book(params)]
    }
    def bookSave(){
        def bookInstance = new Book(params)
        def  filePath
        def  fileName

        MultipartFile f = request.getFile('bookImg')
        if(!f.empty) {
            fileName=f.getOriginalFilename()
            filePath="web-app/images/"
            f.transferTo(new File(filePath+fileName))
        }


        bookInstance.bookImg=fileName
        if(!bookInstance.save(flush: true)){
            render(view: "bookCreate",model: [bookInstance: bookInstance])
        }

        flash.message =message(code: 'default.created.message', args: [message(code: 'book.label', default: 'Book'), bookInstance.id])
        redirect(action: "bookShow", id:bookInstance.id, params: [bookName: bookInstance.bookName])
    }
    def bookShow(Long id) {
        def bookInstance = Book.get(id)
        if (!bookInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'book.label', default: 'Book'), id])
            redirect(action: "roleList")
            return
        }

        [bookInstance: bookInstance]
    }
    //书籍大纲
    def syllabusList(Integer max){
        params.max = Math.min(max ?: 10, 100)
        [syllabusInstanceList: Syllabus.list(params), syllabusInstanceTotal: Syllabus.count()]
    }
    def syllabusCreate(){
        [syllabusInstance: new Syllabus(params)]
    }
    def syllabusSave(){
        def syllabusInstance = new Syllabus(params)
        if (!syllabusInstance.save(flush: true)) {
            render(view: "syllabusCreate", model: [syllabusInstance: syllabusInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'syllabus.label', default: 'Syllabus'), syllabusInstance.id])
        redirect(action: "syllShow", id: syllabusInstance.id)
    }
    def feedbackdelete(Long id) {
        def feedbackInstance = Feedback.get(id)
        if (!feedbackInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'feedback.label', default: 'Feedback'), id])
            redirect(controller: "login",action: "feedback")
            return
        }

        try {
            feedbackInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'feedback.label', default: 'Feedback'), id])
            redirect(controller: "login",action: "feedback")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'feedback.label', default: 'Feedback'), id])
            redirect(action: "show", id: id)
        }
    }


    def upload(){

        def rs=[:]
        def  filePath
        def  fileName
        MultipartFile f = params.imgFile
        if(!f.empty) {
            fileName=f.getOriginalFilename()
            filePath="web-app/images/"
            f.transferTo(new File(filePath+fileName))
        }
        def web='/guihuabao/static/images/'+fileName
        def url=filePath+fileName
        rs=[error:0,url:web]

        if (params.callback) {
            render "${params.callback}(${rs as JSON})"
        } else
            render rs as JSON

    }
}
