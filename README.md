# Multi-Warehouse Management System

This project is a **Multi-Warehouse Management System** focused on accurate inventory tracking and inventory state management. The system supports recording incoming and outgoing goods, managing warehouse permits, handling financial operations, and providing real-time inventory information.

## System Capabilities

The system should be able to answer the following questions:

* What is the current physical inventory of each product in each warehouse?
* How many units of each product are currently on the way to warehouses?
* How many units of each product are reserved for outgoing shipments?
* Is it possible to issue a new incoming or outgoing permit for a specific warehouse and product?

---

# Core Concept: Incoming and Outgoing Permits

Every inventory movement must be authorized through a permit.

There are two types of permits:

### Incoming Permit (Purchase / Receiving)

Example: Purchasing 100 pencils.

An incoming permit is issued. When the goods arrive, the warehouse manager confirms the delivery, and the physical inventory is increased.

### Outgoing Permit (Sale / Shipment)

Example: Selling 50 pencils.

An outgoing permit is issued, and the requested quantity is reserved immediately. Until the warehouse manager confirms the shipment, the physical inventory does not decrease, but the reserved items cannot be sold again.

All permits are initially created with the **Issued** status and change to **Done** after confirmation.

---

# Key Terms

| Term               | Description                                                  |
| ------------------ | ------------------------------------------------------------ |
| Physical Inventory | Products that are physically available in the warehouse      |
| Reserved Stock     | Products allocated to outgoing permits but not yet shipped   |
| Incoming Stock     | Purchased products that have not yet arrived                 |
| Warehouse Capacity | Maximum number of product units that the warehouse can store |

---

# Business Rules

### Available Stock for Shipment

```
Available Stock = Physical Inventory − Total Issued Outgoing Permits
```

### Incoming Stock

```
Incoming Stock = Total Issued Incoming Permits
```

### Capacity Validation

```
Physical Inventory + Incoming Stock + New Quantity ≤ Warehouse Capacity
```

> **Note:** Product transfers between warehouses are **not supported** in this project. Goods can only enter the system from external suppliers and leave through sales or shipments.

---

# Project Modules

## 1. Warehouse Management

Manages multiple warehouses with different characteristics.

Each warehouse contains:

* Unique ID
* Name
* Address
* Capacity
* List of allowed product categories

---

## 2. Permit Management (Core Module)

Each permit contains:

* Unique ID
* Type (Incoming / Outgoing)
* Warehouse
* Product
* Quantity
* Title
* Description
* Date
* Status

---

## 3. Product & Category Management

Each product belongs to exactly one category.

Example categories include:

* Dairy Products
* Fruits & Vegetables
* Dry Food
* Beverages
* Cleaning Products
* Snacks
* Personal Care Products
* Grains & Legumes

---

## 4. Accounting & Sales

Handles purchasing and selling operations while validating:

* Available cash
* Warehouse capacity
* Inventory availability

The module issues the required permits after successful validation.

---

## 5. Reporting

The system provides the following reports:

* Real-time inventory status (Physical, Incoming, Reserved, Available)
* Monthly sales report for each warehouse, including sold items and total revenue
* Pending and completed permit reports
