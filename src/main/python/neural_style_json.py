# Copyright (c) 2015-2017 Anish Athalye. Released under GPLv3.

import os

import numpy as np
import scipy.misc
import tensorflow as tf

from stylize import stylize

import math
from argparse import ArgumentParser

from PIL import Image

import json # Added for json I/O
import sys # For stdin
import vgg # Added to import vgg weights in advance

# default arguments
CONTENT_WEIGHT = 5e0
CONTENT_WEIGHT_BLEND = 1
STYLE_WEIGHT = 5e2
TV_WEIGHT = 1e2
STYLE_LAYER_WEIGHT_EXP = 0.2 # This default value is more appropriate for CPPN styles
LEARNING_RATE = 1e1
BETA1 = 0.9
BETA2 = 0.999
EPSILON = 1e-08
STYLE_SCALE = 1.0
ITERATIONS = 100 # This few iterations is sufficient for CPPN styles
VGG_PATH = 'imagenet-vgg-verydeep-19.mat'
POOLING = 'max'

# Jacob: Added this here so content processing could be done once up front
CONTENT_LAYERS = ('relu4_2', 'relu5_2')

def build_parser():
    parser = ArgumentParser()
    parser.add_argument('--content',
            dest='content', help='content image',
            metavar='CONTENT', required=True)
    parser.add_argument('--styles',
            dest='styles',
            nargs='+', help='one or more style images',
            metavar='STYLE', required=False) # Not required because style images come as json through stdin instead
    parser.add_argument('--output',
            dest='output', help='output path',
            metavar='OUTPUT', required=False) # Made this not required since I don't want to generate output files
    parser.add_argument('--iterations', type=int,
            dest='iterations', help='iterations (default %(default)s)',
            metavar='ITERATIONS', default=ITERATIONS)
    parser.add_argument('--print-iterations', type=int,
            dest='print_iterations', help='statistics printing frequency',
            metavar='PRINT_ITERATIONS')
    parser.add_argument('--checkpoint-output',
            dest='checkpoint_output', help='checkpoint output format, e.g. output%%s.jpg',
            metavar='OUTPUT')
    parser.add_argument('--checkpoint-iterations', type=int,
            dest='checkpoint_iterations', help='checkpoint frequency',
            metavar='CHECKPOINT_ITERATIONS')
    parser.add_argument('--width', type=int,
            dest='width', help='output width',
            metavar='WIDTH')
    parser.add_argument('--style-scales', type=float,
            dest='style_scales',
            nargs='+', help='one or more style scales',
            metavar='STYLE_SCALE')
    parser.add_argument('--network',
            dest='network', help='path to network parameters (default %(default)s)',
            metavar='VGG_PATH', default=VGG_PATH)
    parser.add_argument('--content-weight-blend', type=float,
            dest='content_weight_blend', help='content weight blend, conv4_2 * blend + conv5_2 * (1-blend) (default %(default)s)',
            metavar='CONTENT_WEIGHT_BLEND', default=CONTENT_WEIGHT_BLEND)
    parser.add_argument('--content-weight', type=float,
            dest='content_weight', help='content weight (default %(default)s)',
            metavar='CONTENT_WEIGHT', default=CONTENT_WEIGHT)
    parser.add_argument('--style-weight', type=float,
            dest='style_weight', help='style weight (default %(default)s)',
            metavar='STYLE_WEIGHT', default=STYLE_WEIGHT)
    parser.add_argument('--style-layer-weight-exp', type=float,
            dest='style_layer_weight_exp', help='style layer weight exponentional increase - weight(layer<n+1>) = weight_exp*weight(layer<n>) (default %(default)s)',
            metavar='STYLE_LAYER_WEIGHT_EXP', default=STYLE_LAYER_WEIGHT_EXP)
    parser.add_argument('--style-blend-weights', type=float,
            dest='style_blend_weights', help='style blending weights',
            nargs='+', metavar='STYLE_BLEND_WEIGHT')
    parser.add_argument('--tv-weight', type=float,
            dest='tv_weight', help='total variation regularization weight (default %(default)s)',
            metavar='TV_WEIGHT', default=TV_WEIGHT)
    parser.add_argument('--learning-rate', type=float,
            dest='learning_rate', help='learning rate (default %(default)s)',
            metavar='LEARNING_RATE', default=LEARNING_RATE)
    parser.add_argument('--beta1', type=float,
            dest='beta1', help='Adam: beta1 parameter (default %(default)s)',
            metavar='BETA1', default=BETA1)
    parser.add_argument('--beta2', type=float,
            dest='beta2', help='Adam: beta2 parameter (default %(default)s)',
            metavar='BETA2', default=BETA2)
    parser.add_argument('--eps', type=float,
            dest='epsilon', help='Adam: epsilon parameter (default %(default)s)',
            metavar='EPSILON', default=EPSILON)
    parser.add_argument('--initial',
            dest='initial', help='initial image',
            metavar='INITIAL')
    parser.add_argument('--initial-noiseblend', type=float,
            dest='initial_noiseblend', help='ratio of blending initial image with normalized noise (if no initial image specified, content image is used) (default %(default)s)',
            metavar='INITIAL_NOISEBLEND')
    parser.add_argument('--preserve-colors', action='store_true',
            dest='preserve_colors', help='style-only transfer (preserving colors) - if color transfer is not needed')
    parser.add_argument('--pooling',
            dest='pooling', help='pooling layer configuration: max or avg (default %(default)s)',
            metavar='POOLING', default=POOLING)
    return parser


def main():
    # This will print all array values in full
    np.set_printoptions(threshold=np.nan)

    parser = build_parser()
    options = parser.parse_args()

    if not os.path.isfile(options.network):
        parser.error("Network %s does not exist. (Did you forget to download it?)" % options.network)

    # Load the vgg weights in advance
    vgg_weights, vgg_mean_pixel = vgg.load_net(options.network)
    content_image = imread(options.content)    
    
    # Jacob: moved this here since the same image features will be used for each style image
    content_features = {}
    g = tf.Graph()
    shape = (1,) + content_image.shape
    with g.as_default(), g.device('/cpu:0'), tf.Session() as sess:
        image = tf.placeholder('float', shape=shape)
        net = vgg.net_preloaded(vgg_weights, image, options.pooling)
        content_pre = np.array([vgg.preprocess(content_image, vgg_mean_pixel)])
        for layer in CONTENT_LAYERS:
            content_features[layer] = net[layer].eval(feed_dict={image: content_pre})
    
    print("READY")
    sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input
    count = 0
    #for style in style_images: # loop through separate style inputs individually
    for line in sys.stdin:
        # Assumes a single line of input will be a json for one image
        style = jsonimread(line)
    
        width = options.width
        if width is not None:
            new_shape = (int(math.floor(float(content_image.shape[0]) /
                    content_image.shape[1] * width)), width)
            content_image = scipy.misc.imresize(content_image, new_shape)
        target_shape = content_image.shape
        # This batch of code was in a loop for each style input before
        style_scale = STYLE_SCALE
        if options.style_scales is not None:
            style_scale = options.style_scales[i]
        style = scipy.misc.imresize(style, style_scale *
                target_shape[1] / style.shape[1])

        # Removed code for blanding between multiple styles
        style_blend_weights = [1.0]

        initial = options.initial
        if initial is not None:
            initial = scipy.misc.imresize(imread(initial), content_image.shape[:2])
            # Initial guess is specified, but not noiseblend - no noise should be blended
            if options.initial_noiseblend is None:
                options.initial_noiseblend = 0.0
        else:
            # Neither inital, nor noiseblend is provided, falling back to random generated initial guess
            if options.initial_noiseblend is None:
                options.initial_noiseblend = 1.0
            if options.initial_noiseblend < 1.0:
                initial = content_image

        if options.checkpoint_output and "%s" not in options.checkpoint_output:
            parser.error("To save intermediate images, the checkpoint output "
                         "parameter must contain `%s` (e.g. `foo%s.jpg`)")

        for iteration, image in stylize(
            network=options.network,
            initial=initial,
            initial_noiseblend=options.initial_noiseblend,
            content=content_image,
            styles=[style], # Changed this to be a list of only one style image
            preserve_colors=options.preserve_colors,
            iterations=options.iterations,
            content_weight=options.content_weight,
            content_weight_blend=options.content_weight_blend,
            style_weight=options.style_weight,
            style_layer_weight_exp=options.style_layer_weight_exp,
            style_blend_weights=style_blend_weights,
            tv_weight=options.tv_weight,
            learning_rate=options.learning_rate,
            beta1=options.beta1,
            beta2=options.beta2,
            epsilon=options.epsilon,
            pooling=options.pooling,
            print_iterations=options.print_iterations,
            checkpoint_iterations=options.checkpoint_iterations,
            # These vgg settings are now loaded only once
            vgg_weights=vgg_weights, 
            vgg_mean_pixel=vgg_mean_pixel,
            content_features=content_features
        ):
            output_file = None
            combined_rgb = image
            if iteration is not None:
                if options.checkpoint_output:
                    output_file = options.checkpoint_output % iteration
            else:
                # Change final output files to simply be numbered
                output_file = "%d.JPG" % count
                count = count + 1
            if output_file:
                # No longer save image to file
                #imsave(output_file, combined_rgb)
                # Output json String
                print(json.dumps(combined_rgb.tolist()))
                sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input
    print("DONE")

def jsonimread(jsonImage):
    img = np.array(json.loads(jsonImage))
    return img

# This original version reads from files on disk, but json is preferable
def imread(path):
    img = scipy.misc.imread(path).astype(np.float)
    if len(img.shape) == 2:
        # grayscale
        img = np.dstack((img,img,img))
    elif img.shape[2] == 4:
        # PNG with alpha channel
        img = img[:,:,:3]
    return img

def imsave(path, img):
    img = np.clip(img, 0, 255).astype(np.uint8)
    Image.fromarray(img).save(path, quality=95)

if __name__ == '__main__':
    main()
