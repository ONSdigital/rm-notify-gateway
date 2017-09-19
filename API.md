# Notify Gateway Service API
This page documents the Notify Gateway service API endpoints. Apart from the Service Information endpoint, all these endpoints are secured using HTTP basic authentication. All endpoints return an `HTTP 200 OK` status code except where noted otherwise.

## Service Information
* `GET /info` will return information about this service, collated from when it was last built.

### Example JSON Response
```json
{
  "name": "notifygatewaysvc",
  "version": "10.43.0",
  "origin": "git@github.com:ONSdigital/rm-notify-gateway.git",
  "commit": "2fb601974f875be3f0fb0f9d6c5df0de583b7913",
  "branch": "master",
  "built": "2017-07-15T16:20:00Z"
}
```

## Send Text Message
* `POST /texts/f3778220-f877-4a3d-80ed-e8fa7d104563` will asynchronously send a text message using the GOV.UK Notify text message template with an ID of `f3778220-f877-4a3d-80ed-e8fa7d104563`.

**Required parameters:** `phoneNumber` as the phone number of the recipient.

*Optional parameters:* `personalisation` as a collection of string name/value pairs of GOV.UK Notify template placeholder values, `reference` as a reference string.

### Example JSON Response
```json
{
  "id": "de0da3c1-2cad-421a-bddd-054ef374c6ab",
  "reference": "Test text message",
  "censusUacSmsTemplateId": "f3778220-f877-4a3d-80ed-e8fa7d104563",
  "fromNumber": "07701234567"
}
```

An `HTTP 201 Created` status code is returned if the text message request was successfully enqueued ready for sending by GOV.UK Notify. An `HTTP 400 Bad Request` is returned if the required parameter is missing.


## Send Email
* `POST /emails/290b93f2-04c2-413d-8f9b-93841e684e90` will asynchronously send an email using the GOV.UK Notify email template with an ID of `290b93f2-04c2-413d-8f9b-93841e684e90`.

**Required parameters:** `emailAddress` as the email address of the recipient.

*Optional parameters:* `personalisation` as a collection of string name/value pairs of GOV.UK Notify template placeholder values, `reference` as a reference string.

### Example JSON Response
```json
{
  "id": "845c73f5-e016-4610-8bfe-7699e9f4a3c2",
  "reference": "Test email",
  "censusUacSmsTemplateId": "290b93f2-04c2-413d-8f9b-93841e684e90",
  "fromEmail": "surveys@ons.gov.uk"
}
```

An `HTTP 201 Created` status code is returned if the email request was successfully enqueued ready for sending by GOV.UK Notify. An `HTTP 400 Bad Request` is returned if the required parameter is missing.

## Get Message Status
* `GET /messages/de0da3c1-2cad-421a-bddd-054ef374c6ab` will get the status of the text message or email with an ID of `de0da3c1-2cad-421a-bddd-054ef374c6ab`.

### Example JSON Response
```json
{
  "id": "de0da3c1-2cad-421a-bddd-054ef374c6ab",
  "reference": "Test text message",
  "emailAddress": null,
  "phoneNumber": "07707654321",
  "notificationType": "sms",
  "status": "sent",
  "censusUacSmsTemplateId": "f3778220-f877-4a3d-80ed-e8fa7d104563",
  "templateVersion": 1,
  "createdAt": "2017-07-15T16:25:30Z",
  "sentAt": "2017-07-15T16:28:02Z",
  "completedAt": "2017-07-15T16:28:02Z"
}
```

An `HTTP 404 Not Found` status code is returned if the message with the specified ID could not be found. An `HTTP 503 Service Unavailable` status code is returned if GOV.UK Notify was not able to process the message, perhaps because the service was down.