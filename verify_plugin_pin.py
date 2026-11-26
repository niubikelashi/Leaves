"""构建前校验 plugin-pin/ 字节是否与入库清单一致（防误改 / 防换行规范化）。

用法：E:\\python\\python.exe verify_plugin_pin.py   （在仓库根跑；任何一条不一致就 exit 1）
清单 plugin-pin/SHA256.txt 每行："<sha256>  <相对路径>"，不含清单自身。
"""
import hashlib
import os
import sys

root = os.path.dirname(os.path.abspath(__file__))
manifest = os.path.join(root, "plugin-pin", "SHA256.txt")
if not os.path.exists(manifest):
    print("FAIL: 缺少清单 plugin-pin/SHA256.txt")
    sys.exit(1)

expect = []
for line in open(manifest, encoding="utf-8").read().splitlines():
    line = line.strip()
    if not line or line.startswith("#"):
        continue
    sha, _, path = line.partition("  ")
    if not sha or not path:
        print("FAIL: 清单行格式不对: %r" % line)
        sys.exit(1)
    expect.append((sha.strip(), path.strip()))

bad = []
for sha, path in expect:
    p = os.path.join(root, path.replace("/", os.sep))
    if not os.path.exists(p):
        bad.append("缺失 " + path)
        continue
    got = hashlib.sha256(open(p, "rb").read()).hexdigest()
    if got != sha:
        bad.append("字节不一致 %s 期望 %s 实得 %s" % (path, sha[:16], got[:16]))
    else:
        print("ok %s %d B" % (path, os.path.getsize(p)))

print("清单条目=%d 不一致=%d" % (len(expect), len(bad)))
for b in bad:
    print("FAIL " + b)
if bad:
    sys.exit(1)
print("结论：plugin-pin 字节与入库清单一致，可进入构建")
