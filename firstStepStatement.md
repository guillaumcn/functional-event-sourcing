# 📄 Exercise: Implementing Account Behavior for a Subscription-Based Service with decider pattern

## 🫟 Background

Every user account has a balance and a status that reflects their current payment behavior. Depending on the balance and how many times the user has been previously warned or suspended, their account may:

* remain **active**
* be **temporarily suspended**
* be **permanently closed**

The platform uses three **account statuses** to manage users:

* 🟢 **Billable** – the default status; the account is in good standing or has a manageable overdue balance.
* 🟡 **Suspended** – the account has crossed a risk threshold; the user is temporarily restricted from accessing services.
* 🔴 **Closed** – the account has repeatedly failed to resolve its negative balance; it is permanently deactivated.

---

## 📋 Business Rules

Your task is to implement the logic that governs how an account transitions between these states based on **financial activity**.

### 🤞 1. Initial State

* A newly created account starts with:

  * a **balance** of `0`
  * **status** `Billable`
  * \*\*3 suspension chances\`

---

### 🧾 2. Invoice Processing (Customer is Charged)

* If an account is **Closed**, any attempt to charge it must **fail immediately**.
* If a **Billable** account is charged and the **balance drops below -100**:

  * it becomes **Suspended**
  * and **loses one** suspension chance.
* If a **Suspended** account is charged and the **balance drops below -500**:

  * it is **permanently Closed**.
* If a **Billable** account has **no remaining suspensions** and is charged enough to cross **-100**, it is **directly Closed**.

---

### 💰 3. Payment Processing (Customer Pays)

* If an account is **Closed**, any attempt to accept a payment must **fail immediately**.
* If a **Suspended** account receives a payment and the balance rises to **-100 or above**:

  * it is restored to **Billable**.
* In all other cases, the balance is simply updated **without changing** the account status.

---

## Steps

* Implement the following method in the `Account` class (dirty implementation without any pattern use):

```java
public void updateBalance(BigDecimal amount) {
    // Your implementation goes here
}
```

Your implementation must respect the business rules above.
✅ All provided test cases must pass once your logic is correct.

* Move account state to a value object

* Replace `updateBalance` method with `handleCommand` method that takes an `AccountCommand` parameter and handles the case of an `UpdateBalanceCommand`

* **Only for the status evaluation**, separate decision-making inside a `decide` method and evolution of the state inside a `evolve` method, 
`decide` method should return an event used by `evolve` method to return a new state

* Change the event to an events list and add an `evolveAll` method

* Introduce a new `BalanceUpdatedEvent` and use internal commands to handle decision dependencies problems

---
