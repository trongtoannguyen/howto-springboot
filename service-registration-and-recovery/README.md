# Vấn đề

Trong kiến trúc microservice, việc xử lý thủ công hoặc hard-code thông tin của các service cho mục đích giao tiếp có thể
khó khăn do có hàng trăm service trong 1 hệ thống lớn và dễ bị hỏng nếu thông tin của service được thay đổi trong tương
lai.

# Giải pháp

Cần 1 cơ chế trung gian nhận và lưu trữ các thông tin cần thiết của các service trong hệ thống, đồng thời cung cấp thông
tin này đến các service khi chúng muốn giao tiếp với nhau.

**Service Registry**:

Là 1 database lưu trữ các thông tin cần thiết để giao tiếp như Ip, port, health status của các
service trong hệ thống.

**Service Discovery**:

1. Giúp service đăng ký thông tin của mình lên service
   registry.
2. Tìm kiếm thông tin các dịch vụ đang hoạt động trong hệ thống, ví dụ khi Order-service muốn gọi đến Payment-service nó
   sẽ hỏi Service Registry "Payment đang ở đâu?"

## How to run

1. Make sure required tools for Spring Boot running are installed on your machine.
2. Clone repository

```bash
$ git clone https://github.com/trongtoannguyen/howto-springboot.git
```

3. Open project in an IDE (IntelliJ is recommended)
4. Just run the app and navigate to [localhost:8761](http://localhost:8761) to see Eureka Service Registry dashboard.
