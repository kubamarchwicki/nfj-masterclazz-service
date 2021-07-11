package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        urlPath('/audit/outbox-log')
    }
    response {
        status 200
        body([
                   customerId:
                   value(producer(regex('[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}')))
              ])
        headers {
            header('Content-Type': value(regex('application/json')))
        }
    }
}
