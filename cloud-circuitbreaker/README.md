# About

This guide walks you through the process of applying circuit breakers to potentially-failing method calls using Spring
Cloud Circuit Breaker.

## Dependencies
- Spring Reactive Web
- Spring Cloud Circuit Breaker (with Resilience4J as the implementation)

## What is The Circuit Breaker Pattern?

When we wrap a method call in a circuit breaker, Spring Cloud Circuit Breaker watches for failing calls to that method and,
if failures build up to a specified threshold, Spring Cloud Circuit Breaker opens the circuit so that subsequent calls 
automatically fail. While the circuit is open, Spring Cloud Circuit Breaker redirects calls to the method, and they are passed 
on to our specified fallback method.

## Problems

In distributed systems, making remote calls to external services like a [book recommendation API](./bookstore/src/main/java/example/circuitbreaker/bookstore/BookstoreApplication.java) can lead to cascading failures when those services become unresponsive or fail. Unlike timeout-based methods, which can lead to delayed error responses or the premature failure of healthy requests, repeated attempts to failing services can overwhelm both the calling application and the failing service, degrading overall system performance and user experience.

### Example Scenario

**Without Circuit Breaker:**
```
Request 1:  Call API → Timeout (5s) → Fallback
Request 2:  Call API → Timeout (5s) → Fallback
Request 3:  Call API → Timeout (5s) → Fallback
...100 requests = 500 seconds wasted waiting
```

Each request still attempts the network call, consuming:
- **Time**: Waiting for timeouts (e.g., 5 seconds per request)
- **Resources**: Threads, connections, memory held during waits
- **Cascading effects**: If your service handles 100 requests/second, all 100 threads could be blocked waiting for the failing service

## Solution

The [Circuit Breaker pattern](https://en.wikipedia.org/wiki/Circuit_breaker_design_pattern) is a design pattern that improves system resilience and fault tolerance by monitoring service health and proactively identifying unresponsive services. By wrapping remote calls with a circuit breaker, your application can:

- **Prevent cascading failures**: When failures exceed a threshold, the circuit opens and immediately returns a fallback response without invoking the service
- **Reduce resource consumption**: Avoids wasting time, connections, and threads on calls that will likely fail
- **Enable automatic recovery**: The circuit breaker transitions through three states (Closed → Open → Half-open) to automatically test if the service has recovered
- **Improve user experience**: Provides fast fallback responses instead of long timeouts

### Circuit Breaker States

The circuit breaker operates in three states:

1. **Closed** (Normal operation): Requests pass through to the service. Failures are tracked.
2. **Open** (Service failing): Circuit opens after failure threshold is reached. Requests immediately return fallback without calling the service.
3. **Half-open** (Testing recovery): After a timeout period, the circuit allows a limited number of test requests. If successful, it closes; if they fail, it reopens.

### Example with Circuit Breaker

**With Circuit Breaker:**
```
Request 1-5:  Call API → Failures tracked → Circuit opens
Request 6-100: Circuit OPEN → Immediate fallback (0ms) → No API call
After 60s:     Half-open state → Test request → If fails, reopen; if succeeds, close
```

**Result**: 95 requests returned fallback instantly instead of waiting 5 seconds each, saving 475 seconds of wait time.

The circuit breaker pattern works in conjunction with fallback mechanisms to gracefully handle failures and maintain system stability.