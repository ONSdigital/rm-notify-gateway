# rm-notify-gateway
## evaluation
   This microservice is a web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). 
   This service makes use of 
   [GOV.UK Notify API Java client library](https://docs.notifications.service.gov.uk/java.html#java-client-documentation) 
   to integrate ras-rm for sending emails.  
   
   The service exposes two endpoints:
   * `/emails/{censusUacSmsTemplateId}` - The endpoint provides resource to send email via Gov.Notify for the 
   associated gov.notify templateId.
   * `/messages/{messageId}` - The endpoint to get the status of an existing message.
   
   Along with the endpoints the service also utilises rabbit queues to process emails. There are four channels:
   * `actionInstructionTransformed` - Inbound channel with action instruction for processing emails received via actionSvc.
    The NotifyService looks for associated notify template for action instruction by querying commontemplatesvc endpoint.
   * `actionFeedback` - Outbound channel for action instruction if feedback is required.
   * `notifyRequestTransformed` - Inbound channel to process queued email notification received via endpoint.
   * `notifyRequest` - Outbound channel to queue email notification received via endpoint.
   
   Notify Gateway receives instructions from the [Action Service](https://github.com/ONSdigital/rm-action-service) 
   on the Action.Notify queue in the Rabbit MQ cluster. These are the action request objects and contains XML. At this 
   point the action instructions is picked and processed. As a part of the processing, common template gets queried for 
   notify template id associated with the action instruction. If the feedback is required for action instruction 
   action feedback channel get populated with the response and notify message gets posted to GOV.Notify for the email 
   to be sent. 
   
   Send email endpoint receives the template id in UUID form and as a part of the processing, it gets registered into 
   the message table, a message gets posted to notify request channel to be picked up straight away and  message table 
   gets updated for some reason and notify message gets posted to GOV.Notify for the email to be sent.
   
   Get status endpoint checks if the notification id is present in message table or not. 
   
 ## [data base](notify-gateway-db.png)
   
   `message` is the only table in notify gateway service. Not sure the intention of the table.
  
 ## improvements
   The notify-gateway-svc has a single responsibility i.e. to send emails to gov notify. Notify gateway interacts with 
   common template microservices which is another microservice exposed to return template ids. Looking into the common 
   templates and notify gateway together it seems that there are different templates created in Gov.Notify with same 
   contents for similar action instructions. These template ids can easily be a part of notify-gateway-svc removing the 
   need of another microservice(common-template-svc). 
   This service can be simplified by either improving existing service or rewriting:
   * Remove the use of XSDs and replace with JSON.
   * GetStatus endpoint should be removed or rewritten, making changes to the message table which should hold the information of 
   the status of the notification message.
   * The endpoint places the notification message to a queue to be processed. This can be a sync process.
   * There doesn't seem to be a reason to have two interfaces to client. We can either follow a RESTful or queue approach.
   * common-template-svc and notify-gateway-svc should be merged together.
   * Gov.Notify templates needs to the revisited to remove redundant templates.     