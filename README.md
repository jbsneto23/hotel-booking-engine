# Hotel Booking Engine

A booking engine service for a hotel. It is a REST API to manage hotel inventory rooms, hotel customers and bookings.

### Used Tecnologies and Frameworks

* [Scala 2.13.8](https://www.scala-lang.org)
* [Play Framework 2.8.16](https://www.playframework.com)
* [Slick 3.3.2](https://scala-slick.org)
* [H2 Database 1.4.200](https://www.h2database.com/html/main.html)
* [sbt 1.7.1](https://www.scala-sbt.org)

### Requirements

* Java versions SE 8 through SE 11, inclusive ([Play Framework requirement](https://www.playframework.com/documentation/2.8.x/Requirements))
* [sbt](https://www.scala-sbt.org) installed


### Usage

To run this service locally follow the steps below:

1. `git clone` or download the project file, then navigate to the top level project directory
2. `sbt run` to download dependencies and start the system
3. then, in your HTTP client, access the desired endpoint with the base path [`localhost:9000`](http://localhost:9000)


## Room endpoints

Endpoints to manage hotel inventory rooms.


| Method | Endpoint          | Description                                         |
|--------|-------------------|-----------------------------------------------------|
| GET    | /api/rooms        | List all rooms registered in the inventory          |
| GET    | /api/rooms/:id    | Retrieve a room with a specific id in the inventory |
| POST   | /api/rooms        | Create a room in the inventory                      |
| PUT    | /api/rooms/:id    | Update a room in the inventory                      |
| DELETE | /api/rooms/:id    | Remove a room from the inventory                    |

### Example data

* Input Room

```json
{
    "title": "Simple Suite",
    "description": "Simple suite with a couple bed and a private bathroom.",
    "adultCapacity": 2,
    "childrenCapacity": 0,
    "privateBathroom": true
}
```

* Output Room

```json
{
    "id": 1,
    "title": "Simple Suite",
    "description": "Simple suite with a couple bed and a private bathroom.",
    "adultCapacity": 2,
    "childrenCapacity": 0,
    "privateBathroom": true
}
```

## Customer Endpoints

Endpoints to manage hotel customers. Customers can have room bookings on their behalf.

| Method | Endpoint           | Description                            |
|--------|--------------------|----------------------------------------|
| GET    | /api/customers     | List all customers                     |
| GET    | /api/customers/:id | Retrieve a customer with a specific id |
| POST   | /api/customers     | Create a customer                      |
| PUT    | /api/customers/:id | Update a customer with a specific id   |
| DELETE | /api/customers/:id | Remove a customer with a specific id   |

### Example data

* Input Customer

```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@test.com",
    "phone": "111111111"
}
```

* Output Customer

```json
{
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@test.com",
    "phone": "111111111"
}
```

## Booking Endpoints

Endpoints to manage hotel bookings. A booking is made for a specific room and customer for a period of time.

| Method | Endpoint                                                                  | Description                                                                        |
|--------|---------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| GET    | /api/bookings                                                             | List all bookings                                                                  |
| GET    | /api/bookings/:id                                                         | Retrieve a booking with a specific id                                              |
| POST   | /api/bookings                                                             | Create a booking                                                                   |
| PUT    | /api/bookings/:id                                                         | Update a booking with a specific id                                                |
| DELETE | /api/bookings/:id                                                         | Remove a booking with a specific id                                                |
| GET    | /api/bookings/occupancy?date=yyyy-MM-dd                                   | List the occupancy of rooms on a specific date, which may be occupied or available |
| GET    | /api/bookings/availability?checkInDate=yyyy-MM-dd&checkOutDate=yyyy-MM-dd | List available rooms on the given check-in and check-out dates                     |

### Example data

* Input Booking

```json
{
    "checkInDate": "2022-10-10",
    "checkOutDate": "2022-10-12",
    "checkInTime": "14:00",
    "checkOutTime": "12:00",
    "customerId": 1,
    "roomId": 1
}
```

* Output Booking

```json
{
    "id": 1,
    "checkInDate": "2022-10-10",
    "checkOutDate": "2022-10-12",
    "checkInTime": "14:00:00",
    "checkOutTime": "12:00:00",
    "customer": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "johndoe@test.com",
        "phone": "111111111"
    },
    "room": {
        "id": 1,
        "title": "Simple Suite",
        "description": "Simple suite with a couple bed and a private bathroom.",
        "adultCapacity": 2,
        "childrenCapacity": 0,
        "privateBathroom": true
    },
    "created": "2022-08-29T09:18:30.482162"
}
```

* Output Occupancy

```json
[
    {
        "room": {
            "id": 1,
            "title": "Simple Suite",
            "description": "Simple suite with a couple bed and a private bathroom.",
            "adultCapacity": 2,
            "childrenCapacity": 0,
            "privateBathroom": true
        },
        "date": "2022-10-10",
        "status": "Occupied",
        "booking": {
            "id": 1,
            "checkInDate": "2022-10-10",
            "checkOutDate": "2022-10-12",
            "checkInTime": "14:00:00",
            "checkOutTime": "12:00:00",
            "customer": {
                "id": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "johndoe@test.com",
                "phone": "1111111"
            },
            "room": {
                "id": 1,
                "title": "Simple Suite",
                "description": "Simple suite with a couple bed and a private bathroom.",
                "adultCapacity": 2,
                "childrenCapacity": 0,
                "privateBathroom": true
            },
            "created": "2022-08-29T09:18:30.482162"
        }
    },
    {
        "room": {
            "id": 2,
            "title": "Simple Bedroom",
            "description": "Simple bedroom with a single bed.",
            "adultCapacity": 1,
            "childrenCapacity": 0,
            "privateBathroom": false
        },
        "date": "2022-10-10",
        "status": "Available"
    }
]
```

* Output Availability

```json
{
    "checkInDate": "2022-10-10",
    "checkOutDate": "2022-10-12",
    "availability": [
        {
            "id": 2,
            "title": "Simple Bedroom",
            "description": "Simple bedroom with a single bed.",
            "adultCapacity": 1,
            "childrenCapacity": 0,
            "privateBathroom": false
        }
    ]
}
```