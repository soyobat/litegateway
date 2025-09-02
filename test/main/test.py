from locust import HttpUser, task, between

class GatewayUser(HttpUser):
    # 用户请求间隔
    wait_time = between(1, 3)

    @task
    def ping(self):
        self.client.get("/api/user/ping1")
