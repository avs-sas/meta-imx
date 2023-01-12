DESCRIPTION = "A library to retrieve i.MX GPU performance data"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea25d099982d035af85d193c88a1b479"

SRC_URI[arm-fb.md5sum] = "b1d879a5668e0c0cd83277a0dbba6715"
SRC_URI[arm-fb.sha256sum] = "6a34e01dcc2af30cf2fee37b4aabb4c4d9615e654ffbfbd05f3549f18c885ea0"

SRC_URI[arm-wayland.md5sum] = "7a84eef3d1f509e41e310a4fcdb8ec09"
SRC_URI[arm-wayland.sha256sum] = "b8735f1d477ecb14a7f824d142377ed214db11beab7aef66d9b9a98ccc91d4d0"

SRC_URI[arm-x11.md5sum] = "62788042779d29e9f69931f607c79826"
SRC_URI[arm-x11.sha256sum] = "35fb8d4fb54e0a64d783ee0d602c42b5bc1511f0c8dd4a0946a287cf6247f80b"

SRC_URI[aarch64-fb.md5sum] = "87163a5e983e9016c5c4811ecfc190fb"
SRC_URI[aarch64-fb.sha256sum] = "9ef818398077493551185925974fced3b81aff5bf15e77942a232557229c881e"

SRC_URI[aarch64-wayland.md5sum] = "d8cb884158a227577aac6c9e92ac6853"
SRC_URI[aarch64-wayland.sha256sum] = "a5349930ee41ae71535d417726a843f647cb0d315e67951f9433923287a2c9a8"

SRC_URI[aarch64-x11.md5sum] = "bb862e55b8ee79ee5a83b0119618cd07"
SRC_URI[aarch64-x11.sha256sum] = "85c1b51d33e5939600af311d509191387b864db2e0b55e11347b93831e662228"

inherit fsl-eula-unpack2 fsl-eula-graphics

PACKAGE_ARCH = "${MACHINE_SOCARCH}"

RDEPENDS:${PN} = "imx-gpu-viv"

# Compatible only with i.MX with GPU
COMPATIBLE_MACHINE        = "(^$)"
COMPATIBLE_MACHINE:imxgpu = "${MACHINE}"
