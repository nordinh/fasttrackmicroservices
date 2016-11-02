# Course notes

## Day 1

### Identifying services
Try focusing services around business capabilities: what a business does vs how a business does it. (cfr. bounded context)
The 'what' is far more stable than the how, making it much more appropriate to design services around.
Challenge: get business people to focus on what instead of how!

**Technique to do this is using/building a capability map (multiple hierarchies!) --> cfr epics in story map**

### *Old school* SOA
Presents good on powerpoint slides: BPM services, business services and data services integrated with each other.
 - **However**: Does not scale, when the number of BPM services increases so does the number of integrations.
 - **Solution**: ESB? Hides all integrations in a magic box, but they do not disappear and stuff gets harder.
It also does not scale, it becomes one giant hub for all integrations.

### Web as a platform
Except from POST all verbs are idempotent (albeit not safe!) and can thus be retried in the case of failure = easiest recovery strategy possible!
See slides and RFC 2616 for all the details on verbs, headers and status codes.
 - **JSON-LD**: JSON for linked documents, hypermedia format in JSON
 - **HAL**: JSON Hypertext Application Language

### Summary
For the past decade(s) we have implemented a lot of stuff above HTTP(S) like WS-*, security protocols, ...
In reality the HTTP protocol already provides solutions for most of those problems, and they are proven at global scale
 