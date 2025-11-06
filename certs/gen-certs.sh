
echo "🔹 生成 CA 根证书..."
openssl genrsa -out ca-key.pem 4096
openssl req -x509 -new -nodes \
  -key ca-key.pem \
  -sha256 -days 3650 \
  -out ca-cert.pem \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=CA/CN=BitMSCloudCA"

echo "🔹 生成服务端私钥和证书请求..."
openssl genrsa -out server-key.pem 2048
openssl req -new -key server-key.pem -out server.csr \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=Server/CN=service.bitms"

# ✅ 添加 mac、win、wsl 各节点的 DNS 及 IP
cat > server-ext.cnf <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
# --- 本机回环地址 ---
DNS.2 = localhost
IP.1 = 127.0.0.1

# --- mac 节点 ---
DNS.3 = bg-mac-mini.tailfbfced.ts.net
IP.2 = 100.120.86.63

# --- Windows 节点 ---
DNS.4 = bg-camellia.tailfbfced.ts.net
IP.3 = 100.97.223.54

# --- WSL 节点 ---
DNS.5 = bg-windows-wsl2.tailfbfced.ts.net
IP.4 = 100.113.43.94
EOF

echo "🔹 签发服务端证书..."
openssl x509 -req -in server.csr \
  -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial \
  -out server-cert.pem -days 1095 -sha256 -extfile server-ext.cnf

echo "🔹 导出服务端 keystore..."
openssl pkcs12 -export \
  -in server-cert.pem \
  -inkey server-key.pem \
  -out server-keystore.p12 \
  -name "server" \
  -CAfile ca-cert.pem \
  -caname "bitmscloudca" \
  -password pass:changeit

echo "🔹 生成客户端 (gateway) 私钥和 CSR..."
openssl genrsa -out gateway-key.pem 2048
openssl req -new -key gateway-key.pem -out gateway.csr \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=Gateway/CN=gateway.bitms"

cat > gateway-ext.cnf <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = clientAuth
EOF

echo "🔹 签发客户端证书..."
openssl x509 -req -in gateway.csr \
  -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial \
  -out gateway-cert.pem -days 1095 -sha256 -extfile gateway-ext.cnf

echo "🔹 导出客户端 keystore..."
openssl pkcs12 -export \
  -in gateway-cert.pem \
  -inkey gateway-key.pem \
  -out gateway-keystore.p12 \
  -name "gateway" \
  -CAfile ca-cert.pem \
  -caname "bitmscloudca" \
  -password pass:changeit

echo "🔹 创建 truststore..."
keytool -importcert -trustcacerts \
  -alias bitmscloudca \
  -file ca-cert.pem \
  -keystore ca-truststore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -noprompt

echo "✅ 所有证书生成完毕！"
ls -l *.pem *.p12
