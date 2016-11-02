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

