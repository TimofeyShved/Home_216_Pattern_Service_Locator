package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	    // Паттерн (сервис локатор) позволяет работать с несколькими сервисами одновремено,
        // причём он их кеширует

        Service service = ServiceLocator.getService("SERVICE1");
        service.execute();
        Service serviceTwo = ServiceLocator.getService("SERVICE2");
        serviceTwo.execute();
    }
}

// есть 2 сервиса
interface Service{
    String getName();
    void execute();
}

class Service1 implements Service{
    @Override
    public String getName() {
        return "Service1";
    }

    @Override
    public void execute() {
        System.out.println("Executing " + this.getName());
    }
}

class Service2 implements Service{
    @Override
    public String getName() {
        return "Service2";
    }

    @Override
    public void execute() {
        System.out.println("Executing " + this.getName());
    }
}

// локатор который всё это инициализирует, но в зависимости от выбраного сервиса
class InitialContext {
    public Object lookup (String jndiName){
        if (jndiName.equalsIgnoreCase("SERVICE1")){
            return new Service1();
        } else if (jndiName.equalsIgnoreCase("SERVICE2")){
            return new Service2();
        }
        return null;
    }
}

// здесь мы кешируем данные, достаём данные если они есть и добавляем в кеш если нету
class Cache {
    private List<Service> services = new ArrayList<>();

    public Service getService(String serviceName){
        for (Service s: services){
            if (s.getName().equalsIgnoreCase(serviceName)){
                System.out.println("Cached "+serviceName);
                return s;
            }
        }
        return null;
    }

    public void addService(Service newSevice){
        boolean exists = false;

        for (Service s: services){
            if (s.getName().equalsIgnoreCase(newSevice.getName())){
                exists = true;
            }
        }
        if (!exists){
            services.add(newSevice);
        }
    }
}

// Сервис локатор, который контролирует всю работу
class ServiceLocator{
    private static Cache cache;

    static {
        cache = new Cache();
    }

    public static Service getService(String jndiName){
        Service service = cache.getService(jndiName);
        // если нашёл сервис то вернёт его
        if (service!=null){
            return service;
        }

        // в противном случае создаст
        InitialContext context = new InitialContext();
        Service newService = (Service) context.lookup(jndiName);
        cache.addService(newService);
        return newService;
    }
}