SUMMARY = "ARM Neural Network SDK"
DESCRIPTION = "Linux software and tools to enable machine learning (Caffe/Tensorflow) workloads on power-efficient devices"
LICENSE = "MIT & Apache-2.0"
# Apache-2.0 license applies to mobilenet tarball
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e14a924c16f7d828b8335a59da64074 \
                    file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

#PR = "r1"
PV = "20.08"

ARMNN_SRC ?= "git://source.codeaurora.org/external/imx/armnn-imx.git;protocol=https"
SRCBRANCH = "branches/armnn_20_08"
SRCREV = "a9de15b5faed05dfa8f94030060bac1e0df0f21d" 

SRCREV_FORMAT = "armnn"

S = "${WORKDIR}/git"

inherit python3native
inherit cmake

SRC_URI = " \
    ${ARMNN_SRC};branch=${SRCBRANCH} \
    http://download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=mobilenet;subdir=${WORKDIR}/tfmodel;destsuffix=tfmodel \
    file://0001-AIR-3570_pyarmnn-yocto-cross-compile.patch \
"

SRC_URI[mobilenet.md5sum] = "d5f69cef81ad8afb335d9727a17c462a"
SRC_URI[mobilenet.sha256sum] = "1ccb74dbd9c5f7aea879120614e91617db9534bdfaa53dfea54b7c14162e126b"

DEPENDS = " \
    boost \
    protobuf \
    stb \
    half \
"
RDEPENDS_MX8       = ""
RDEPENDS_MX8_mx8   = "nn-imx"
RDEPENDS_MX8_mx8mm = ""
RDEPENDS_MX8_mx8mnlite = ""
RDEPENDS_${PN}   = " \
    arm-compute-library \
    protobuf \
    boost \
    ${RDEPENDS_MX8} \
"
PACKAGECONFIG_VSI_NPU       = ""
PACKAGECONFIG_VSI_NPU_mx8   = "vsi-npu"
PACKAGECONFIG_VSI_NPU_mx8mm = ""
PACKAGECONFIG_VSI_NPU_mx8mnlite = ""

PACKAGECONFIG ??= "neon ref caffe tensorflow tensorflow-lite onnx tests pyarmnn ${PACKAGECONFIG_VSI_NPU}"

PACKAGECONFIG[caffe] = "-DBUILD_CAFFE_PARSER=1 -DCAFFE_GENERATED_SOURCES=${STAGING_DATADIR}/armnn-caffe,-DBUILD_CAFFE_PARSER=0,armnn-caffe"
PACKAGECONFIG[neon] = "-DARMCOMPUTENEON=1 -DARMCOMPUTE_LIBRARY_RELEASE=${STAGING_LIBDIR}/libarm_compute.so -DARMCOMPUTE_CORE_LIBRARY_RELEASE=${STAGING_LIBDIR}/libarm_compute_core.so,-DARMCOMPUTENEON=0,arm-compute-library"
PACKAGECONFIG[onnx] = "-DBUILD_ONNX_PARSER=1 -DONNX_GENERATED_SOURCES=${STAGING_DATADIR}/armnn-onnx ,-DBUILD_ONNX_PARSER=0,armnn-onnx"
PACKAGECONFIG[opencl] = "-DARMCOMPUTECL=1,-DARMCOMPUTECL=0,opencl-headers"
PACKAGECONFIG[tensorflow] = "-DBUILD_TF_PARSER=1 -DTF_GENERATED_SOURCES=${STAGING_DATADIR}/armnn-tensorflow,-DBUILD_TF_PARSER=0, armnn-tensorflow "
PACKAGECONFIG[tensorflow-lite] = "-DTF_LITE_SCHEMA_INCLUDE_PATH=${STAGING_DATADIR}/armnn-tensorflow-lite -DTF_LITE_GENERATED_PATH=${STAGING_DATADIR}/armnn-tensorflow-lite -DBUILD_TF_LITE_PARSER=1 ,-DBUILD_TF_LITE_PARSER=0, flatbuffers armnn-tensorflow"
PACKAGECONFIG[unit-tests] = "-DBUILD_UNIT_TESTS=1,-DBUILD_UNIT_TESTS=0"
PACKAGECONFIG[tests] = "-DBUILD_TESTS=1,-DBUILD_TESTS=0"
PACKAGECONFIG[ref] = "-DARMNNREF=1,-DARMNNREF=0"
PACKAGECONFIG[vsi-npu] = "-DVSI_NPU=1,-DVSI_NPU=0,nn-imx"
PACKAGECONFIG[pyarmnn] = ",,armnn-swig-native python3-native python3-pip-native python3-wheel-native python3-setuptools-native"

EXTRA_OECMAKE += " \
    -DSHARED_BOOST=1 \
    -DHALF_INCLUDE=${STAGING_DIR_HOST} \
"

ARMNN_INSTALL_DIR = "${bindir}/${P}"
PYARMNN_INSTALL_DIR = "${ARMNN_INSTALL_DIR}/pyarmnn"

do_compile_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'pyarmnn', 'true', 'false', d)}; then
        # copy required to link against pyarmnn wrappers
        # due to a bug in python/setuptools an explicit path cannot be set 
        # and default libdir must be used
        cp -Rf ${WORKDIR}/build/libarmnnTfParser.so* ${STAGING_LIBDIR}
        cp -Rf ${WORKDIR}/build/libarmnnTfLiteParser.so* ${STAGING_LIBDIR}
        cp -Rf ${WORKDIR}/build/libarmnnOnnxParser.so* ${STAGING_LIBDIR}
        cp -Rf ${WORKDIR}/build/libarmnnCaffeParser.so* ${STAGING_LIBDIR}
        cp -Rf ${WORKDIR}/build/libarmnn.so* ${STAGING_LIBDIR}

        export SWIG_EXECUTABLE=${STAGING_BINDIR_NATIVE}/swig
        export ARMNN_INCLUDE=${S}/include
        export ARMNN_LIB=${WORKDIR}/build

        cd ${S}/python/pyarmnn
        ${PYTHON} setup.py clean --all
        ${PYTHON} swig_generate.py -v
        ${PYTHON} setup.py build_ext --inplace
        # Need to create 2 wheels one being installed by the native env and the second to copy to the aarch64 image
        # for users. Sadly contents are the same, but user cannot install the native wheel on target.
        ${PYTHON} setup.py bdist_wheel
        ${PYTHON} setup.py bdist_wheel --plat-name linux_aarch64
    fi
}

do_install_append() {
    # test applications (examples)
    if ${@bb.utils.contains('PACKAGECONFIG', 'tests', 'true', 'false', d)}; then
        install -d ${D}${ARMNN_INSTALL_DIR}
        CP_ARGS="-Prf --preserve=mode,timestamps --no-preserve=ownership"        
        find ${WORKDIR}/build/tests -maxdepth 1 -type f -executable -exec cp $CP_ARGS {} ${D}${ARMNN_INSTALL_DIR} \;
        chrpath -d ${D}${ARMNN_INSTALL_DIR}/*
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'pyarmnn', 'true', 'false', d)}; then
        # install native python wheel
        export PIP_DISABLE_PIP_VERSION_CHECK=1
        export PIP_NO_CACHE_DIR=1
        install -d ${D}/${PYTHON_SITEPACKAGES_DIR}
        ${STAGING_BINDIR_NATIVE}/pip3 install -v \
            -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-deps \
            ${S}/python/pyarmnn/dist/pyarmnn-*_x86_64.whl
        find ${D}/${PYTHON_SITEPACKAGES_DIR} -type d -name "__pycache__" -exec rm -Rf {} +

        # copy aarch64 python wheel for use in virtual environments
        install -d ${D}${PYARMNN_INSTALL_DIR}
        install -m 0555 ${S}/python/pyarmnn/dist/pyarmnn-*_aarch64.whl ${D}${PYARMNN_INSTALL_DIR}
        
        # pyarmnn examples for eiq
        cp -R ${S}/python/pyarmnn/examples ${D}${PYARMNN_INSTALL_DIR}
        
        # pyarmnn unit tests
        cp -R ${S}/python/pyarmnn/test ${D}${PYARMNN_INSTALL_DIR}
        rm -f ${D}${PYARMNN_INSTALL_DIR}/test/test_setup.py # not supposed to be run on target
        cp -R ${S}/python/pyarmnn/scripts ${D}${PYARMNN_INSTALL_DIR}
        install -m 0555 ${S}/python/pyarmnn/conftest.py ${D}${PYARMNN_INSTALL_DIR}
    fi
}

CXXFLAGS += "-fopenmp"
LIBS += "-larmpl_lp64_mp"

PACKAGE_BEFORE_PN = "${PN}-tests"

FILES_${PN} += "${libdir}/python*"
FILES_${PN} += "${PYARMNN_INSTALL_DIR}/examples"

FILES_${PN}-tests = "${PYARMNN_INSTALL_DIR}/test ${PYARMNN_INSTALL_DIR}/scripts"
FILES_${PN}-tests += "${PYARMNN_INSTALL_DIR}/conftest.py"

# We support i.MX8 only (for now)
COMPATIBLE_MACHINE = "(mx8)"
