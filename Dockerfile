FROM debian:stretch-slim
COPY ./target/tv4-image /app/tv4-image
WORKDIR /app
CMD ["./tv4-image"]
