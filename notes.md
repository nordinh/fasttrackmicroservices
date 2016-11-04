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

## Day 2

### Exercise 1
Writing a REST client that places an order and pays for it. Payload was XML, which makes it harder in languages like Java
in which you usually map xml to objects

### Scalability
Stateless services, because otherwise (at scale) replication is needed in a (n/2 + 1) way (n=#machines) to make sure session
or whatever is replicated to prevent race conditions. You cannot Memcache or Hazelcast your way out of this (because 
asynchronous replication).
 - [Collapsed Forwarding](http://wiki.squid-cache.org/Features/CollapsedForwarding) to avoid spikes and group multiple requests for the same resource.
 - [Cache channels](https://github.com/mnot/squid-channels) as an event bus for controlling the cache
 
### Event-driven archictecture
Atom feeds! Only use if no low-latency requirements, because latency is traded off for scalability and availability.
In case of a massive amount of events and you do not want your clients to read them all, you can fall back to
truncation/compaction/snapshots to represent the point in time where a certain state applies and take that as a starting
point to consume events again. Clients need to be aware of those different kind of events/entries.

### Security
Use plain basic authentication in combination with HTTPS. Another option discussed in 'Rest in Practice' is an atom feed over HTTP
with entries encrypted by use of a shared secret. However those atom feeds are cached forever, opening the door for brute force
attacks (which has become much less expensive with the trend of affordable GPU rental by e.g. Amazon).
OAuth as a nice alternative for authentication, e.g. works also really wel for SSO.

## Day 3

### Distributed systems
Distributed systems are hard and should be avoided when possible. Why do we still want to do it? High availability, high throughput and data redundancy.
ACID transactions in a distributed system are a DOS attack because of locking and coordination.
 - A distributed commit log such as Apache Kafka can help here, it's writes are append only and one can only read in the order that items have been written (no random access)
which makes it performant, o(1) vs O(n).
 - Raft as consensus protocol for the *distributed* commit log. Raft = ties logs together
Distributed transactions: try making an explicit resource with which you can interact in a transactional way, i.e. one resource to interact with.

