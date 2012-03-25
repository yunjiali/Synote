package org.synote.config

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ConfigurationController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [configurationInstanceList: Configuration.list(params), configurationInstanceTotal: Configuration.count()]
    }

    def create = {
        def configurationInstance = new Configuration()
        configurationInstance.properties = params
        return [configurationInstance: configurationInstance]
    }

    def save = {
        def configurationInstance = new Configuration(params)
        if (configurationInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'configuration.label', default: 'Configuration'), configurationInstance.id])}"
            redirect(action: "show", id: configurationInstance.id)
        }
        else {
            render(view: "create", model: [configurationInstance: configurationInstance])
        }
    }

    def show = {
        def configurationInstance = Configuration.get(params.id)
        if (!configurationInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
            redirect(action: "list")
        }
        else {
            [configurationInstance: configurationInstance]
        }
    }

    def edit = {
        def configurationInstance = Configuration.get(params.id)
        if (!configurationInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [configurationInstance: configurationInstance]
        }
    }

    def update = {
        def configurationInstance = Configuration.get(params.id)
        if (configurationInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (configurationInstance.version > version) {
                    
                    configurationInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'configuration.label', default: 'Configuration')] as Object[], "Another user has updated this Configuration while you were editing")
                    render(view: "edit", model: [configurationInstance: configurationInstance])
                    return
                }
            }
            configurationInstance.properties = params
            if (!configurationInstance.hasErrors() && configurationInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'configuration.label', default: 'Configuration'), configurationInstance.id])}"
                redirect(action: "show", id: configurationInstance.id)
            }
            else {
                render(view: "edit", model: [configurationInstance: configurationInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def configurationInstance = Configuration.get(params.id)
        if (configurationInstance) {
            try {
                configurationInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'configuration.label', default: 'Configuration'), params.id])}"
            redirect(action: "list")
        }
    }
}
