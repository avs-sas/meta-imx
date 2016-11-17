FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS_append_imxgpu2d = " virtual/libg2d"
DEPENDS_append_mx8 = " virtual/libgles2 virtual/libg2d virtual/kernel"

GST_CFLAGS_EXTRA = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', \
                       bb.utils.contains('DISTRO_FEATURES', 'wayland', '-DEGL_API_FB -DWL_EGL_PLATFORM', '-DEGL_API_FB', d),d)}"
CFLAGS_append_mx8 = " ${GST_CFLAGS_EXTRA}"

PACKAGECONFIG_append_mx6q = " opencv"
PACKAGECONFIG_append_mx6qp = " opencv"
PACKAGECONFIG_append_mx8 = " opencv"
PACKAGECONFIG_remove_mx6sl = " gles2"

#revert poky fido commit:cdc2c8aeaa96b07dfc431a4cf0bf51ef7f8802a3: move EGL to Wayland
PACKAGECONFIG[gles2]   = "--enable-gles2 --enable-egl,--disable-gles2 --disable-egl,virtual/libgles2 virtual/egl"
PACKAGECONFIG[wayland] = "--enable-wayland --disable-x11,--disable-wayland,wayland"

SRC_URI_append_imxpxp = " \
    file://0001-glplugin-Change-wayland-default-res-to-1024x768.patch \
    file://0002-Support-fb-backend-for-gl-plugins.patch \
    file://0003-Add-directviv-to-glimagesink-to-improve-playback-per.patch \
    file://0004-MMFMWK-6930-glplugin-Accelerate-gldownload-with-dire.patch \
    file://0005-Fix-dependence-issue-between-gst-plugin-.patch \
    file://0006-glcolorconvert-convert-YUV-to-RGB-use-directviv.patch \
    file://0007-glwindow-work-around-for-no-frame-when-imxplayer-use.patch \
    file://0008-glplugin-glcolorconvert-fix-MRT-cannot-work-in-GLES3.patch \
    file://0009-MMFMWK-7308-Fix-build-issue-on-non-GPU-soc.patch \
"

#common
COMMON_IMX = " file://0001-mpegtsmux-Need-get-pid-when-create-streams.patch \
                   file://0002-mpeg4videoparse-Need-detect-picture-coding-type-when.patch \
                   file://0003-mpegvideoparse-Need-detect-picture-coding-type-when-.patch \
                   file://0004-modifiy-the-videoparse-rank.patch \
                   file://0005-glfilter-Lost-frame-rate-info-when-fixate-caps.patch \
                   file://0006-opencv-Add-video-stitching-support-based-on-Open-CV.patch \
                   file://0007-camerabin-Add-one-property-to-set-sink-element-for-v.patch \
                   file://0008-Fix-for-gl-plugin-not-built-in-wayland-backend.patch \
                   file://0011-glplugin-gl-wayland-fix-loop-test-hang-in-glimagesin.patch \
                   file://0012-glplugin-Fix-glimagesink-wayland-resize-showed-blurr.patch \
                   file://0015-support-video-crop-for-glimagesink.patch \
                   file://0016-Add-fps-print-in-glimagesink.patch \
                   file://0026-MMFMWK-7151-glplugin-glimagesink-support-video-rotat.patch \
                   file://0028-ion-DMA-Buf-allocator-based-on-ion.patch \
                   file://0029-EGL_DMA_Buf-Wrong-attribute-list-type-for-EGL-1.5.patch \
                   file://0030-glimagesink-Fix-horizontal-vertical-flip-matrizes.patch \
                   file://0031-glwindow-Fix-glimagesink-cannot-show-frame-when-conn.patch \
                   file://0033-ion_allocator-refine-ion-allocator-code.patch \
                   file://0035-videocompositor-Remove-output-format-alpha-check.patch \
                   file://0036-Add-ion-memory-support-for-glupload.patch \
                   file://0037-Support-one-texture-for-YUV-format-in-dmabuf-upload.patch \
                   file://0038-Add-ion-dmabuf-support-in-gldownload.patch \
                   file://0039-imx8dv-fix-qmlgltest-can-not-run-on-FB-b.patch \
"

SRC_URI_append_imxgpu2d = "${COMMON_IMX}"
SRC_URI_append_imxpxp = "${COMMON_IMX}"

# include fragment shaders
FILES_${PN}-opengl += "/usr/share/*.fs"

PACKAGE_ARCH_imxpxp = "${MACHINE_SOCARCH}"
PACKAGE_ARCH_mx8 = "${MACHINE_SOCARCH}"

# Fix libgstbadion-1.0.so.0 which is under built directory cannot be found
do_compile_prepend () {
    export GIR_EXTRA_LIBS_PATH="${B}/gst-libs/gst/ion/.libs"
}
