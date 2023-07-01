DESCRIPTION = "A library to retrieve i.MX GPU performance data"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=673fa34349fa40f59e0713cb0ac22b1f"

IMX_SRCREV_ABBREV = "e4ba456"

SRC_URI[arm-fb.md5sum] = "6747bc995533ee3dc675c9efda6e3a35"
SRC_URI[arm-fb.sha256sum] = "69e81bb80f63faf1210d9302de90203585b2353e4d314ca6a83e1a4f3aa60ed5"

SRC_URI[arm-wayland.md5sum] = "512c3c018b2b97996cd7e87bf82fa34c"
SRC_URI[arm-wayland.sha256sum] = "c13f7891735039095527ae4c13e8c730776d5ceb84a9a23a9b99a379512e1ac0"

SRC_URI[arm-x11.md5sum] = "62788042779d29e9f69931f607c79826"
SRC_URI[arm-x11.sha256sum] = "35fb8d4fb54e0a64d783ee0d602c42b5bc1511f0c8dd4a0946a287cf6247f80b"

SRC_URI[aarch64-fb.md5sum] = "87163a5e983e9016c5c4811ecfc190fb"
SRC_URI[aarch64-fb.sha256sum] = "9ef818398077493551185925974fced3b81aff5bf15e77942a232557229c881e"

SRC_URI[aarch64-wayland.md5sum] = "181a45a27cd9fe372449e4ead37504e5"
SRC_URI[aarch64-wayland.sha256sum] = "89bf91705780d0029799112fe130c86a05ea3585ad0b7257395f641c22f45e7f"

SRC_URI[aarch64-x11.md5sum] = "bb862e55b8ee79ee5a83b0119618cd07"
SRC_URI[aarch64-x11.sha256sum] = "85c1b51d33e5939600af311d509191387b864db2e0b55e11347b93831e662228"

inherit fsl-eula-unpack2 fsl-eula-graphics fsl-eula-recent

PACKAGE_ARCH = "${MACHINE_SOCARCH}"

RDEPENDS:${PN} = "imx-gpu-viv"

# Compatible only with i.MX with GPU
COMPATIBLE_MACHINE        = "(^$)"
COMPATIBLE_MACHINE:imxgpu = "${MACHINE}"
